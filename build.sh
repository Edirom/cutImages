#!/bin/bash

usage ()
{
  echo 'Usage : Script -p <prefix> -s <size>'
  exit
}

while [ "$1" != "" ]; do
case $1 in
        -p )           shift
                       PREFIX=$1
                       ;;
        -s )           shift
                       TILESIZE=$1
                       ;;
    esac
    shift
done

if [ -x /opt/ImageMagick ]; then
	echo "Cut image with ImageMagick"
else
	echo "ImageMagick is not installed"
fi

echo "Search images in directory"
for file in `ls *.JPG *.jpg *.bmp *.BMP *.gif *.GIF *.WBMP *.png *.PNG *.jpeg *.wbmp *.JPEG`
	 do 
	 echo "file: $file"
	 # file type
	 filename=$(basename "$file")
	 extension="${filename##*.}"
	 filename="${filename%.*}"	 
	 mkdir -p "$filename"
	 newDirectory=$filename
	 echo "cut in directory: $newDirectory"

# Get image size
height=$(/opt/ImageMagick/bin/identify -format %H $file)
width=$(/opt/ImageMagick/bin/identify -format %W $file)

echo "$width x $height"

if [ "$TILESIZE" = "" ]; then
	TILESIZE=256
fi

level=$(($width/$TILESIZE))

if [ $width -ge $height ]; then
	level=$(($height/$TILESIZE))
fi

minSize=$height
if [ $width -ge $height ]; then
	minsize=$width
fi

maxLevel=$level
newZoom=0

while [ $minSize -ge $TILESIZE ]
	do
	minSize=$((minSize / 2))
	newZoom=$(($newZoom+1))
done
		
echo "zoom levels $newZoom"

# If width or height greater than tile size -> cut
while [ $width -ge $TILESIZE ] || [ $height -ge $TILESIZE ]
do	
	if [ $level == $maxLevel ]; then
		/opt/ImageMagick/bin/convert $file -crop "$TILESIZE"x"$TILESIZE" \
		          -set filename:tile $newZoom"-%[fx:page.x/$TILESIZE]-%[fx:page.y/$TILESIZE]" \
		          +repage +adjoin $newDirectory"/"$PREFIX%[filename:tile]"."$extension
		newZoom=$(($newZoom-1))
		level=$(($level-1))
		# minimize size to 50%
		/opt/ImageMagick/bin/convert $file -resize 50% $newZoom"-ZOOM-"$file
	else
		/opt/ImageMagick/bin/convert $newZoom"-ZOOM-"$file -crop "$TILESIZE"x"$TILESIZE" \
		          -set filename:tile $newZoom"-%[fx:page.x/$TILESIZE]-%[fx:page.y/$TILESIZE]" \
		          +repage +adjoin $newDirectory"/"$PREFIX%[filename:tile]"."$extension		
		levelOld=$level
		level=$(($level-1))
		newZoomOld=$newZoom
		newZoom=$(($newZoom-1))		
		# minimized Image minimize to 50%
		/opt/ImageMagick/bin/convert $newZoomOld"-ZOOM-"$file -resize 50% $newZoom"-ZOOM-"$file
	fi
		
	# Get image size
	height=$(/opt/ImageMagick/bin/identify -format %H $newZoom"-ZOOM-"$file)
	width=$(/opt/ImageMagick/bin/identify -format %W $newZoom"-ZOOM-"$file)
	
	echo "$width x $height"
done

mv "0-ZOOM-"$file $newDirectory"/"$PREFIX"0-0-0."$extension

rm *-ZOOM-*

done

