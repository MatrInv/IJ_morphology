These IMAGEJ plugins are made to extract some morphological properties from shapes and are supposed to be used on images with only two grayscale levels representing closed shapes, like shown in the directory "img_tests".

-Squelettisation.java gives the skeleton of the shape by applying a mask that computes the yokoi numbers.

-AxeMedian_DT[4 or 8].java computes and draws the median axis of the shape by applying a maximum local filter over the map of the chanfrein distances in [4 or 8]-connexity.

-DT[4 or 8]_ChanfreinSquel.java computes and draws the map of the chanfrein distances by using a chanfrein mask in [4 or 8]-connexity

-Caracterisiques_Simples.java computes and prints the geometrical caracteristics of the shape : area, perimeter, center of gravity, bounding box, diameter, perimeter, isoperimetric relation, elongation.

-FreemanCode.java computes and prints the freeman chain code of the shape.

To download IMAGEJ : https://github.com/imagej .
To execute these plugins, put them into the directory /home/.imagej/plugins, open an image in the software IMAGEJ, then click "plugins" -> "compile and run", search and select the plugin you want to use.
