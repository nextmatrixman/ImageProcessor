# ImageProcessor
Pex code challenge - prevalent color finder

This program takes 3 parameters:
1. Working directory - where the URL file is located, as well as where the downloaded images and the output file will be
2. Input filename - name of the URL file
3. Output filename - name of the CSV file where the result will be stored

Example: "/Users/dshen/Documents/ImageProcessor/src/Input/" "urls.txt" "output.csv"

This program reads in a list of image URLs from an input file and generates 3 most prevalent colors in the RGB scheme in hexadecimal format (#000000 - #FFFFFF) in each image.
Then outputs the result to a CSV in the format of "url,color,color,color".