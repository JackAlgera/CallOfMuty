## Install

```
git clone https://github.com/JackAlgera/CallOfMuty.git
cd CallOfMuty
```

If you are on Windows please execute this (say no to Vim!)
```
git config core.editor notepad
```

## After modification

Before any modifications, do not forget to download the last version of the game using
```
git pull
```

## After modification

Launch ```git add -A```
Check that modified files are staged for commits ```git status```
After that, commit your files with ```git commit -m "Perfect message describing my changes"```
And finally, to upload your modified files on server execute:
```git push```

## Adding resources

To add an image to the game, put it in src/resources/images
If it is a tileSet, make sure to create the right variable on top of the Tools class, you will then be able to select the tile you want with Tools.selectTile
If not, you will then be able to load it with Tools.loadImage or Tools.loadIcon if you want an ImageIcon

To add an audio file to the game, put it in src/resources/audio