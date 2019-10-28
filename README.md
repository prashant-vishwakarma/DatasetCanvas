# DatasetCanvas
Android App with minimal Canvas Interface to save characters handrwritten on screen for Dataset Collection Purpose

## Dependency
App uses Android CanvasView of Predefined Dimensions(By the Developer).
User can Draw An Image on Screen as alternative to writing on Paper and Scanning the image.

## Why Use such App
Using the app to collect data helps skip preprocessing steps on scanned images.

To make a list of items to Collect, an array resource can be found in resources/arrays.xml
This list is shown on screen as selectable list items.

Once an item from List is selecte and save button clicked, app saves current Canvas View to Phone Storage in folder DatasetCanvas/
PNG format in RGB is used to save images. Image name depends on the index of current Item selected in list.

Images can then be sent to the Developer Email as found in resources/strings.xml

## Screenshots

![image](https://user-images.githubusercontent.com/47455409/67678105-cc128380-f9ab-11e9-8c4e-6551004cc71c.png) ![image](https://user-images.githubusercontent.com/47455409/67678219-1b58b400-f9ac-11e9-9c31-ad1e95e28b3b.png) ![image](https://user-images.githubusercontent.com/47455409/67678307-5529ba80-f9ac-11e9-8042-ade212aebc80.png)
