# DatasetCanvas
Android App with minimal Canvas Interface to save characters handrwritten on screen for Dataset Collection Purpose

App uses Android CanvasView of Predefined Dimensions(By the Developer).
User can Draw An Image on Screen as alternative to writing on Paper and Scanning the image.

Using the app to collect data helps skip preprocessing steps on scanned images.

To make a list of items to Collect, an array resource can be found in resources/arrays.xml
This list is shown on screen as selectable list items.

Once an item from List is selecte and save button clicked, app saves current Canvas View to Phone Storage in folder DatasetCanvas/
PNG format in RGB is used to save images. Image name depends on the index of current Item selected in list.

Images can then be sent to the Developer Email as found in resources/strings.xml
