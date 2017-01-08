docker build . -t josm/josm
docker run -it --name josm -v $PWD\test:/josm/test josm/josm
docker rm josm
docker rmi josm/josm
