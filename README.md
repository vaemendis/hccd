# HCCD - HTML+CSS Card Designer

HCCD is a small tool designed to automate the creation of game cards from a CSV (Excel) file using a few lines of HTML and CSS.

**[Download it here](https://github.com/vaemendis/hccd/releases)**

![](http://vaemendis.github.io/external/hccd/hccd-diagram.png)

For other card game creation tools (there are a lot of them), check [this list](https://boardgamegeek.com/thread/991506/resources-card-game-makers) on BoardGameGeek.

# 30 seconds example
Here is how to use HCCD:
- Download and unzip the [latest HCCD release ](https://github.com/vaemendis/hccd/releases) (the **hccd-binary.zip** file)  
- Double click on **Hccd.jar** to launch the application (you need to have installed [Java](https://java.com/en/download/) first)  
- Now, in HCCD, open the file `character.html` from `examples/example-full` (or just drag and drop it on the user interface).
A new file (`character-GENERATED.html`, the cards contact sheet) has been generated. You just created your first set of cards. (here is an [online example](http://vaemendis.github.io/external/hccd/character-GENERATED.html))
- You can now edit the card layout and description files (the contact sheet is automatically updated):
  - `character.html`
  - `character.css`
  - `character.csv`
 
# How it works

To build the card contact sheet, HCCD uses three files and as many image as you need:
- **the HTML file**: contains the layout for a single type card, with variables declared using mustaches (e.g. `{{myVariable}}`). The custom code has to be declared in the `div` with the `card` class (see the HTML file in  `examples/example-minimum`)
- **the CSS file**: contains the style to apply to the HTML layout. As your cards will be printed, be careful to use physical units (`mm`, `pt`...) instead of pixels to define dimensions.
- **the CSV file**: contains all the data specific to each card. The first line of the CSV file is the header: it contains the name of the columns (same names as the variables you have defined in your HTML file). Starting from the second line, a new card will be generated for every line.
- **images**: can be referenced from the HTML or CSS files

**IMPORTANT**: the three files (HTML, CSS, CSV) must have the *same name* (with a different extensions) and be in the same directory as the images.

When you open your HTML file, HCCD will monitor it, as well as the two other files (CSS and CSV), so that each time one of these three files is modified the card contact sheet is automatically regenerated. If the contact sheet is already opened in your browser, just refresh it ot see your modifications.

So once your HTML file is opened, just forget about HCCD and work on your files !

## Settings

Settings are modified in the interface.

![](http://vaemendis.github.io/external/hccd/hccd-screenshot.png)

#### Rows/Columns
Dimensions of the grid of cards. You have to choose it depending on your card size and page size so that it fits in a single page (HCCD will create as many grids and pages as necessary). 

#### CSV format
**RFC-4180** is standard CSV format. If you use Excel and have issues reading the CSV file, try changing the format to **Excel.**

#### Delimiter
Must obviously match the delimiter used in your CSV file

#### Card filter
Usefull when you want to reprint only a part of your cards. Choose the card to print using comma separated values (and a dash for ranges).

#### Refresh button
Only usefull to apply new settings, as file modifications are automatically detected anyway.


# Useful tips

- Don't forget to use `mm` and `pt` instead of `px` in your CSS.
- You don't need to hit the "Refresh" button each time you modify one of your description files (html, css, csv), HCCD monitors them and the contact sheet will be regenerated automatically.
- If you want to fully automate your workflow, you can use an add-on like [Tab Auto Reload](https://addons.mozilla.org/en-US/firefox/addon/tab-auto-reload/) for Firefox.
- The CSV file has to be UTF-8 encoded if you are using non-ASCII characters (like accents)
- When printing the contact sheet using your browser, don't forget to adapt settings so that the page is printed at 100% of its size, with background images but any without margin or header.
- If you get `[NOT FOUND]` on your cards instead of the text you had put into your CSV file, check that the delimiter and the CSV file type are correctly set in the interface.

## License
HCCD is licensed under GPL V3.

HCCD uses icons from [game-icons.net](http://game-icons.net/) and the [JarClassLoader](http://www.jdotsoft.com/JarClassLoader.php) from JDotSoft.

## Disclaimer

As you might have gathered from the ugly user interface, the ugly code and the lack of unit tests, HCCD is a small, quickly put together tool made to meet my specific needs. It is not meant to evolve, except for a few bug fixes if need be.
