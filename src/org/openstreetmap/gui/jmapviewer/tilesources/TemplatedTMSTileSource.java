// License: GPL. For details, see Readme.txt file.
package org.openstreetmap.gui.jmapviewer.tilesources;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openstreetmap.gui.jmapviewer.interfaces.TemplatedTileSource;

/**
 * Handles templated TMS Tile Source. Templated means, that some patterns within
 * URL gets substituted.
 *
 * Supported parameters
 * {zoom} - substituted with zoom level
 * {z} - as above
 * {NUMBER-zoom} - substituted with result of equation "NUMBER - zoom",
 *                  eg. {20-zoom} for zoom level 15 will result in 5 in this place
 * {zoom+number} - substituted with result of equation "zoom + number",
 *                 eg. {zoom+5} for zoom level 15 will result in 20.
 * {x} - substituted with X tile number
 * {y} - substituted with Y tile number
 * {!y} - substituted with Yahoo Y tile number
 * {-y} - substituted with reversed Y tile number
 * {switch:VAL_A,VAL_B,VAL_C,...} - substituted with one of VAL_A, VAL_B, VAL_C. Usually
 *                                  used to specify many tile servers
 * {header:(HEADER_NAME,HEADER_VALUE)} - sets the headers to be sent to tile server
 */
public class TemplatedTMSTileSource extends TMSTileSource implements TemplatedTileSource {

    private Random rand;
    private String[] randomParts;
    private final Map<String, String> headers = new HashMap<>();
    private boolean inverse_zoom = false;
    private int zoom_offset = 0;

    // CHECKSTYLE.OFF: SingleSpaceSeparator
    private static final String COOKIE_HEADER   = "Cookie";
    private static final String PATTERN_ZOOM    = "\\{(?:(\\d+)-)?z(?:oom)?([+-]\\d+)?\\}";
    private static final String PATTERN_X       = "\\{x\\}";
    private static final String PATTERN_Y       = "\\{y\\}";
    private static final String PATTERN_Y_YAHOO = "\\{!y\\}";
    private static final String PATTERN_NEG_Y   = "\\{-y\\}";
    private static final String PATTERN_SWITCH  = "\\{switch:([^}]+)\\}";
    private static final String PATTERN_HEADER  = "\\{header\\(([^,]+),([^}]+)\\)\\}";
    private static final Pattern PATTERN_PARAM  = Pattern.compile("\\{((?:(?:(?:\\d+)-)?(z)(?:oom)?(?:[+-]\\d+)?)|(x)|(y)|(!y)|(-y))\\}");
    // CHECKSTYLE.ON: SingleSpaceSeparator

    private static final String[] ALL_PATTERNS = {
        PATTERN_HEADER, PATTERN_ZOOM, PATTERN_X, PATTERN_Y, PATTERN_Y_YAHOO, PATTERN_NEG_Y, PATTERN_SWITCH
    };

    /**
     * Creates Templated TMS Tile Source based on ImageryInfo
     * @param info imagery info
     */
    public TemplatedTMSTileSource(TileSourceInfo info) {
        super(info);
        String cookies = info.getCookies();
        if (cookies != null && !cookies.isEmpty()) {
            headers.put(COOKIE_HEADER, cookies);
        }
        handleTemplate();
    }

    private void handleTemplate() {
        // Capturing group pattern on switch values
        Matcher m = Pattern.compile(".*"+PATTERN_SWITCH+".*").matcher(baseUrl);
        if (m.matches()) {
            rand = new Random();
            randomParts = m.group(1).split(",");
        }
        Pattern pattern = Pattern.compile(PATTERN_HEADER);
        StringBuffer output = new StringBuffer();
        Matcher matcher = pattern.matcher(baseUrl);
        while (matcher.find()) {
            headers.put(matcher.group(1), matcher.group(2));
            matcher.appendReplacement(output, "");
        }
        matcher.appendTail(output);
        baseUrl = output.toString();
        m = Pattern.compile(".*"+PATTERN_ZOOM+".*").matcher(this.baseUrl);
        if (m.matches()) {
            if (m.group(1) != null) {
                inverse_zoom = true;
                zoom_offset = Integer.parseInt(m.group(1));
            }
            if (m.group(2) != null) {
                String ofs = m.group(2);
                if (ofs.startsWith("+"))
                    ofs = ofs.substring(1);
                zoom_offset += Integer.parseInt(ofs);
            }
        }

    }

    @Override
    public Map<String, String> getHeaders() {
        return headers;
    }

    @Override
    public String getTileUrl(int zoom, int tilex, int tiley) {
        StringBuffer url = new StringBuffer(baseUrl.length());
        Matcher matcher = PATTERN_PARAM.matcher(baseUrl);
        while (matcher.find()) {
            String replacement = "replace";
            switch (matcher.group(1)) {
            case "z": // PATTERN_ZOOM
                replacement = Integer.toString((inverse_zoom ? -1 * zoom : zoom) + zoom_offset);
                break;
            case "x": // PATTERN_X
                replacement = Integer.toString(tilex);
                break;
            case "y": // PATTERN_Y
                replacement = Integer.toString(tiley);
                break;
            case "!y": // PATTERN_Y_YAHOO
                replacement = Integer.toString((int) Math.pow(2, zoom-1)-1-tiley);
                break;
            case "-y": // PATTERN_NEG_Y
                replacement = Integer.toString((int) Math.pow(2, zoom)-1-tiley);
                break;
            default:
                replacement = '{' + matcher.group(1) + '}';
            }
            matcher.appendReplacement(url, replacement);
        }
        matcher.appendTail(url);
        return url.toString().replace(" ", "%20");
    }

    /**
     * Checks if url is acceptable by this Tile Source
     * @param url URL to check
     */
    public static void checkUrl(String url) {
        assert url != null && !"".equals(url) : "URL cannot be null or empty";
        Matcher m = Pattern.compile("\\{[^}]*\\}").matcher(url);
        while (m.find()) {
            boolean isSupportedPattern = false;
            for (String pattern : ALL_PATTERNS) {
                if (m.group().matches(pattern)) {
                    isSupportedPattern = true;
                    break;
                }
            }
            if (!isSupportedPattern) {
                throw new IllegalArgumentException(
                        m.group() + " is not a valid TMS argument. Please check this server URL:\n" + url);
            }
        }
    }
}
