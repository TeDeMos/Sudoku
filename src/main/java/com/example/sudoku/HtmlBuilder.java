package com.example.sudoku;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class HtmlBuilder {
    public void createHtml(int[][] level){
        try {
            FileWriter myWriter = new FileWriter("sudoku.html");
            myWriter.write("<!DOCTYPE html>\n" +
                    "<html lang=\"en\">\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <style>\n" +
                    "        .cell {\n" +
                    "            display: flex;\n" +
                    "            width: 26px;\n" +
                    "            height: 26px;\n" +
                    "            border: solid black;\n" +
                    "            border-radius: 0;\n" +
                    "            border-width: 2px;\n" +
                    "            margin: 0;\n" +
                    "            padding: 0;\n" +
                    "            font-size: 20px;\n" +
                    "            cursor: pointer;\n" +
                    "            text-align: center;\n" +
                    "        }\n" +
                    "        .sq1 {\n" +
                    "            display: flex;\n" +
                    "            flex-direction: row;\n" +
                    "            flex-wrap: wrap;\n" +
                    "            width: 90px;\n" +
                    "            min-width: 90px;\n" +
                    "            min-height: 90px;\n" +
                    "            height: 90px;\n" +
                    "            border: solid black;\n" +
                    "            border-width: 1px;\n" +
                    "            margin: 1px;\n" +
                    "        }\n" +
                    "        .sq2 {\n" +
                    "            display: flex;\n" +
                    "            flex-direction: row;\n" +
                    "            flex-wrap: wrap;\n" +
                    "            width: 284px;\n" +
                    "            height: 284px;\n" +
                    "            margin: 50px;\n" +
                    "        }\n" +
                    "    </style>\n" +
                    "    <title>Title</title>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "    <div class=\"sq2\">\n" +
                    "        <div class=\"sq1\">\n" +
                    "            <div class=\"cell\">" + level[0][0] + "</div>\n" +
                    "            <div class=\"cell\">" + level[0][1] + "</div>\n" +
                    "            <div class=\"cell\">" + level[0][2] + "</div>\n" +
                    "            <div class=\"cell\">" + level[0][3] + "</div>\n" +
                    "            <div class=\"cell\">" + level[0][4] + "</div>\n" +
                    "            <div class=\"cell\">" + level[0][5] + "</div>\n" +
                    "            <div class=\"cell\">" + level[0][6] + "</div>\n" +
                    "            <div class=\"cell\">" + level[0][1] + "</div>\n" +
                    "            <div class=\"cell\">" + level[0][1] + "</div>\n" +
                    "        </div>\n" +
                    "        <div class=\"sq1\">\n" +
                    "            <div class=\"cell\">" + level[0][0] + "</div>\n" +
                    "            <div class=\"cell\">" + level[0][0] + "</div>\n" +
                    "            <div class=\"cell\">" + level[0][0] + "</div>\n" +
                    "            <div class=\"cell\">" + level[0][0] + "</div>\n" +
                    "            <div class=\"cell\">" + level[0][0] + "</div>\n" +
                    "            <div class=\"cell\">" + level[0][0] + "</div>\n" +
                    "            <div class=\"cell\">" + level[0][0] + "</div>\n" +
                    "            <div class=\"cell\">" + level[0][0] + "</div>\n" +
                    "            <div class=\"cell\">" + level[0][0] + "</div>\n" +
                    "        </div>\n" +
                    "        <div class=\"sq1\">\n" +
                    "            <div class=\"cell\">" + level[0][0] + "</div>\n" +
                    "            <div class=\"cell\">" + level[0][0] + "</div>\n" +
                    "            <div class=\"cell\">" + level[0][0] + "</div>\n" +
                    "            <div class=\"cell\">" + level[0][0] + "</div>\n" +
                    "            <div class=\"cell\">" + level[0][0] + "</div>\n" +
                    "            <div class=\"cell\">" + level[0][0] + "</div>\n" +
                    "            <div class=\"cell\">" + level[0][0] + "</div>\n" +
                    "            <div class=\"cell\">" + level[0][0] + "</div>\n" +
                    "            <div class=\"cell\">" + level[0][0] + "</div>\n" +
                    "        </div>\n" +
                    "        <div class=\"sq1\">\n" +
                    "            <div class=\"cell\">" + level[0][0] + "</div>\n" +
                    "            <div class=\"cell\">" + level[0][0] + "</div>\n" +
                    "            <div class=\"cell\">" + level[0][0] + "</div>\n" +
                    "            <div class=\"cell\">" + level[0][0] + "</div>\n" +
                    "            <div class=\"cell\">" + level[0][0] + "</div>\n" +
                    "            <div class=\"cell\">" + level[0][0] + "</div>\n" +
                    "            <div class=\"cell\">" + level[0][0] + "</div>\n" +
                    "            <div class=\"cell\">" + level[0][0] + "</div>\n" +
                    "            <div class=\"cell\">" + level[0][0] + "</div>\n" +
                    "        </div>\n" +
                    "        <div class=\"sq1\">\n" +
                    "            <div class=\"cell\">" + level[0][0] + "</div>\n" +
                    "            <div class=\"cell\">" + level[0][0] + "</div>\n" +
                    "            <div class=\"cell\">" + level[0][0] + "</div>\n" +
                    "            <div class=\"cell\">" + level[0][0] + "</div>\n" +
                    "            <div class=\"cell\">" + level[0][0] + "</div>\n" +
                    "            <div class=\"cell\">" + level[0][0] + "</div>\n" +
                    "            <div class=\"cell\">" + level[0][0] + "</div>\n" +
                    "            <div class=\"cell\">" + level[0][0] + "</div>\n" +
                    "            <div class=\"cell\">" + level[0][0] + "</div>\n" +
                    "        </div>\n" +
                    "        <div class=\"sq1\">\n" +
                    "            <div class=\"cell\">" + level[0][0] + "</div>\n" +
                    "            <div class=\"cell\">" + level[0][0] + "</div>\n" +
                    "            <div class=\"cell\">" + level[0][0] + "</div>\n" +
                    "            <div class=\"cell\">" + level[0][0] + "</div>\n" +
                    "            <div class=\"cell\">" + level[0][0] + "</div>\n" +
                    "            <div class=\"cell\">" + level[0][0] + "</div>\n" +
                    "            <div class=\"cell\">" + level[0][0] + "</div>\n" +
                    "            <div class=\"cell\">" + level[0][0] + "</div>\n" +
                    "            <div class=\"cell\">" + level[0][0] + "</div>\n" +
                    "        </div>\n" +
                    "        <div class=\"sq1\">\n" +
                    "            <div class=\"cell\">" + level[0][0] + "</div>\n" +
                    "            <div class=\"cell\">" + level[0][0] + "</div>\n" +
                    "            <div class=\"cell\">" + level[0][0] + "</div>\n" +
                    "            <div class=\"cell\">" + level[0][0] + "</div>\n" +
                    "            <div class=\"cell\">" + level[0][0] + "</div>\n" +
                    "            <div class=\"cell\">" + level[0][0] + "</div>\n" +
                    "            <div class=\"cell\">" + level[0][0] + "</div>\n" +
                    "            <div class=\"cell\">" + level[0][0] + "</div>\n" +
                    "            <div class=\"cell\">" + level[0][0] + "</div>\n" +
                    "        </div>\n" +
                    "        <div class=\"sq1\">\n" +
                    "            <div class=\"cell\">" + level[0][0] + "</div>\n" +
                    "            <div class=\"cell\">" + level[0][0] + "</div>\n" +
                    "            <div class=\"cell\">" + level[0][0] + "</div>\n" +
                    "            <div class=\"cell\">" + level[0][0] + "</div>\n" +
                    "            <div class=\"cell\">" + level[0][0] + "</div>\n" +
                    "            <div class=\"cell\">" + level[0][0] + "</div>\n" +
                    "            <div class=\"cell\">" + level[0][0] + "</div>\n" +
                    "            <div class=\"cell\">" + level[0][0] + "</div>\n" +
                    "            <div class=\"cell\">" + level[0][0] + "</div>\n" +
                    "        </div>\n" +
                    "        <div class=\"sq1\">\n" +
                    "            <div class=\"cell\">" + level[0][0] + "</div>\n" +
                    "            <div class=\"cell\">" + level[0][0] + "</div>\n" +
                    "            <div class=\"cell\">" + level[0][0] + "</div>\n" +
                    "            <div class=\"cell\">" + level[0][0] + "</div>\n" +
                    "            <div class=\"cell\">" + level[0][0] + "</div>\n" +
                    "            <div class=\"cell\">" + level[0][0] + "</div>\n" +
                    "            <div class=\"cell\">" + level[0][0] + "</div>\n" +
                    "            <div class=\"cell\">" + level[0][0] + "</div>\n" +
                    "            <div class=\"cell\">" + level[0][0] + "</div>\n" +
                    "        </div>\n" +
                    "    </div>\n" +
                    "\n" +
                    "</body>\n" +
                    "</html>");
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
    public static void main(String[] Args){
        HtmlBuilder b = new HtmlBuilder();
        b.createHtml();// level[][]
    }
}
