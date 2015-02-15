Cut Images in tiles 
=============

This project provide to cut images in tiles with the range of Z, X, Y and pixel size of each tile.
It is used to load and display tile layers on the map, defined with Leaflet library <http://leafletjs.com> 
Project contains:
* bash script
* Java application
* Eclipse project

Dependencies
------------

* For bash script: http://www.imagemagick.org
* For Eclipse project: https://eclipse.org (luna, java 1.8)

Bash Script
------------
Script cut images with ImageMagick application. 
Prameters: 
mandatory: -d <directory> 
optional: -p <prefix for tiles name> -s <size for each tiles, default: 256>

Java Application (java 1.8)
------------
MAC: start with <java -XstartOnFirstThread -jar cutImage.jar> from jar-directory.
Selection of image or image-directory for cut more images, define prefix for tiles name and size for tile (default: 256)

Eclipse Project
------------
Luna with java 1.8



