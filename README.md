# ViceSampleApplication

Hey thanks for checking out this sample app for Vice Media! Its a simple image grabber that google searches images based on keywords provided by the user

Features:
Lazy loading: implemented lazy loading so that images will load into the grid after they have been downloaded. But since we have information about the size of the images, I layout a frame for the image before it even gets downloaded
endOfScroll Loader: implemented a way for a scroll listener to load more pages of content from google when user has scrolled to bottom of grid. well add more images dynamically
Staggered grid View: I used Etsy's open source gridView for handling different size images in the grid. Useful b/x it handles livecycle methods that the regular gridView wont do as well. also it allows for the colum rows to be independent of eachother
ImageViewer: if you click on an image it will animate from inside the grid view to a page of its own. you can navigate back from there to the grid of images
SerachBarFragment: implemented the searchBar as a fragment so that it could do extra layout/display things easily and contained within its functionality. communicates to activity when need to google search


Bugs:
display is weird when looking on a Phone in landscape (I only have a phone to test so I cant say what it lookslke on tablet)
Images wil load from the bottom of the page up, snstead of top down
the search bar looks a little scrapped together. There was more styling I could have done, but did not have so much time
will only load 8 pages of images from google. There is an option to load more, but requires navigation to a different link to the Json of more pages. I would have implemented this in the endOfScroll Loader but did not have time.



