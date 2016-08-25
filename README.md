#lwAnn

lwAnn is a basic image annotation tool intended to make it easy and quick to annotate scenes. Aside from the usual stuff you might expect, the main feature is data persistence, which means everything you do is saved as you do it. You can close the program, re-open it later, move back and forth between scenes in the dataset, all without having to worry about saving anything.

Quickstart guide:
- From the file menu, navigate to the root directory of the dataset you want to start labeling. lwAnn will find all jpg files under any root directory you give it.

- Start labeling by clicking "add label" and typing in a name. While you have the label selected in the list on the right, left clicking will allow you to draw on the image with a randomly selected colour corresponding to the selected label. Right clicking with a label selected will allow you to make use of a very rough eraser function to tidy up mistakes.

- To switch to another label, simply click it in the list, or add a new one.

- You can alter the brush size from the drop-down menu, to make big or small jobs easier.

- When you're done with a scene, just press left or right to move on to the next or previous scene in the dataset. You can move backwards or forwards through the data as you wish, and any changes you make will persist, though the label pallette does not persist across scenes.

KEYBOARD SHORTCUTS:

- ctrl+z - infinite undo

- h - hides all drawing on the image
- j - renders all drawing on the image at 50% opacity
- k - cycles the size of the brush
