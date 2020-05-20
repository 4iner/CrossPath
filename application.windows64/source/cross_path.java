import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class cross_path extends PApplet {

Object[][] board;
int rect_size;
int rows;
int cols;
int mx;
int my;
final int DIR_UP = 0;
final int DIR_RIGHT = 1;
final int DIR_DOWN = 2;
final int DIR_LEFT = 3;
PShape s;
public void settings(){
  rect_size = 100;
  loadBoard("Levels/Master_1.txt");
  size(rect_size * rows, rect_size * cols);
}
public void setup() {
  makeCirc();
}

public void draw() {
  background(255);
  boolean allZeros = true;
  int empties = 0;
  s.setStroke(true);
  for (int x = 0; x < rows; x++) {
    for (int y = 0; y < cols; y++) {
      int bx = x * rect_size; //bx = bigger x
      int by = y * rect_size; //by = bigger y
      noFill();
      rect(bx, by, rect_size, rect_size);

      if (board[x][y] instanceof Empty) {

        empties++;
      } else if (board[x][y] instanceof Circle) {
        Circle c = (Circle) board[x][y];
        fill(c.col);
        if (allZeros && c.n != 0)
          allZeros = false;
        textAlign(CENTER);
        s.setFill(c.col);
        //ellipse(bx + 50, by + 50, rect_size, rect_size);
        shape(s, bx, by);
        fill(255);
        textSize(rect_size/2);
        text(c.n, bx + rect_size/2, by+rect_size/2 + textAscent()*5/12);
      } else if (board[x][y] instanceof Flow) {
        Flow f = (Flow) board[x][y];
        Circle c = f.c;
        fill(c.col);
        if (f.edge) {
          //float r = red(c.col);
          //float g = green(c.col);
          //float b = blue(c.col);
          //fill(r - 25, g - 25, b - 25);
          if (f.dir == DIR_UP) {
            for (int i = 1; i < abs (f.y - c.y); i++) {
              rect(bx + rect_size/4, (y + i) * rect_size, rect_size/2, rect_size);
            }
            arc(bx + rect_size/2, by + rect_size, rect_size/2, rect_size* 3/2, PI, 2 * PI);
          } else if (f.dir == DIR_DOWN) {
            for (int i = 1; i < abs (f.y - c.y); i++) {
              rect(bx + rect_size/4, (y - i) * rect_size, rect_size/2, rect_size);
            }
            arc(bx + rect_size/2, by + 1, rect_size/2, rect_size* 3/2, 0, PI);
          } else if (f.dir == DIR_LEFT) {
            for (int i = 1; i < abs (f.x - c.x); i++) {
              rect((x + i) * rect_size, by+ rect_size/4, rect_size, rect_size/2);
            }
            arc(bx+rect_size, by  + rect_size/2, rect_size* 3/2, rect_size/2, PI/2, 3 * PI/2 );
          } else if (f.dir == DIR_RIGHT) {
            for (int i = 1; i < abs (f.x - c.x); i++) {
              rect((x - i) * rect_size, by + rect_size/4, rect_size, rect_size/2);
            }
            arc(bx + 1, by  + rect_size/2, rect_size * 3/2,  rect_size/2, 3 * PI/2, 5 * PI/2 );
          }
        }
      } else if (board[x][y] instanceof Block) {
        fill(125);
        rect(bx, by, rect_size, rect_size);
      }
    }
  }
  if (allZeros && empties == 0) {
    rect(width/4, height/4, width/2, height/2);
  }
  //noLoop();
}
public void makeCirc(){
  s = createShape();
  s.beginShape();
  s.fill(0, 0, 255);
  s.noStroke();
  s.vertex(0, rect_size/4);
  s.vertex(0, rect_size * 3 / 4);
  //int r = rect_size/2;
  //for(float i = PI; i >= PI/2; i-= PI/4){
  //    int x = rect_size/2 + floor(r * cos(i));
  //    int y = rect_size/2 + floor(r * sin(i));
  //    println(x+":"+y);
  //    s.vertex(x,y);
  //}
  s.vertex(rect_size/4, rect_size);
  s.vertex(rect_size/4*3, rect_size);
  s.vertex(rect_size,rect_size/4*3);
  s.vertex(rect_size,rect_size/4);
  s.vertex(rect_size/4*3,0);
  s.vertex(rect_size/4,0);
  s.endShape(CLOSE);
}
public void loadBoard(String filename) {
  BufferedReader in = createReader(filename);
  String line = "";
  try {
    line = in.readLine();
    String[] sizes = line.split(" ");
    rows = PApplet.parseInt(sizes[0]);
    cols = PApplet.parseInt(sizes[1]);
    board = new Object[rows][cols];
    int y = 0;
    while ((line = in.readLine())!=null) {
      //print(line+"\n");
      String[] splitted = line.split(" ");
      int x = 0;
      for (String s : splitted) {
        int n = PApplet.parseInt(s);
        if (n == 0) {
          board[x][y] = new Empty(x, y);
        } else if (n > 0) {
          board[x][y] = new Circle(x, y, n);
        } else {
          board[x][y] = new Block(x, y);
        }
        x++;
      }
      y++;
    }
    in.close();
  } 
  catch(Exception e) {
  }
}
public void updateMouseCoords() {
  mx = mouseX/rect_size;
  my = mouseY/rect_size;
  if (mx >= rows) mx = rows - 1;
  if (my >= cols) my = cols - 1;
}
boolean cLocked = false; //locked on a circle
int cx; //x of locked circle
int cy; //y of locked circle 
boolean eLocked = false; //locked on an edge
int ex;
int ey;
public void mousePressed() {
  updateMouseCoords();
  if (board[mx][my] instanceof Circle) {
    cx = mx;
    cy = my;
    cLocked = true;
  } else if (board[mx][my] instanceof Flow) {
    Flow f = (Flow)board[mx][my];
    if (f.edge) {
      eLocked = true;
      ex = mx;
      ey = my;
    }
  }
}
public void mouseReleased() {
  updateMouseCoords();
  if (cLocked) {
    Circle c = (Circle) board[cx][cy];
    //print("released this");
    if (abs(mx - cx) > 0 && my == cy) {
      int dir = mx - cx > 0 ? 1 : -1;
      boolean cf = true; //check forward; if true, path is empty
      for (int i = 1; i < abs(mx - cx) + 1; i++) {
        if (!(board[cx + i * dir][cy] instanceof Empty)) {
          cf = false;
          break;
        }
      }
      if (cf) {
        for (int i = 1; i < abs(mx - cx); i++) {
          board[cx + i * dir][cy] = new Flow(cx + i * dir, cy, c, false);
          c.n--;
        }
        board[mx][cy] = new Flow(mx, cy, c, true);
        c.n--;
      }
    } else if (abs(my - cy) > 0 && mx == cx) {
      int dir = my - cy > 0 ? 1 : -1;
      boolean cf = true; //check forward; if true, path is empty
      for (int i = 1; i < abs(my - cy) + 1; i++) {
        if (!(board[cx][cy + i * dir] instanceof Empty)) {
          cf = false;
          break;
        }
      }
      if (cf) {
        for (int i = 1; i < abs(my - cy); i++) {
          board[cx][cy + i * dir] = new Flow(cx, cy + i * dir, c, false);
          c.n--;
        }
        board[cx][my] = new Flow(cx, my, c, true);
        c.n--;
      }
    }
    cLocked = false;
  } else if (eLocked) {
    Flow f = (Flow) board[ex][ey]; 
    Circle c = f.c;
    //print("released this");
    //if (c.x == mx && c.y == my) {
    //}
    if (abs(mx - ex) > 0 && my == ey) {
      int dir = mx - ex > 0 ? 1 : -1;
      boolean cf = true; //check forward; if true, path is empty
      if (c.x == mx && c.y == my) {
      } else if ((ex < mx && c.x < ex) || (ex > mx && c.x > ex)) {
        for (int i = 1; i < abs(mx - ex) + 1; i++) {
          if (!(board[ex+ i * dir][ey] instanceof Empty)) {
            cf = false;
            break;
          }
        }
        // println("done loop!");
      } else {
        for (int i = 0; i < abs(mx - ex) + 1; i++) {
          if (!(board[ex + i * dir][ey] instanceof Flow)) {
            cf = false;
            break;
          }
        }
      }
      if (cf) {
        //print("works");
        if ((ex < mx && c.x < ex) || (ex > mx && c.x > ex)) {

          for (int i = 0; i < abs(mx - ex); i++) {
            board[ex+ i * dir][ey] = new Flow(ex+ i * dir, ey, c, false);
            c.n--;
          }
          board[mx][ey] = new Flow(mx, ey, c, true);
          //  if(abs(my-ey) != 1) c.n--;
        } else {
          for (int i = 0; i < abs(mx - ex); i++) {
            board[ex+ i * dir][ey ] = new Empty(ex+ i * dir, ey );
            c.n++;
          }
          if (board[mx][ey] instanceof Flow) {
            Flow k = (Flow) board[mx][ey];
            if (k.c == c)
              board[mx][ey] = new Flow(mx, ey, c, true);
          }
        }
        // c.n--;
      }
    } else if (abs(my - ey) > 0 && mx == ex) {
      //println("my: "+my+", ey: "+ey+", cy: "+c.y);
      // println(abs(my - ey) < abs(my - c.y));
      int dir = my - ey > 0 ? 1 : -1;
      boolean cf = true; //check forward; if true, path is empty
      if (c.x == mx && c.y == my) {
      } else if ((ey < my && c.y < ey) || (ey > my && c.y > ey)) {
        for (int i = 1; i < abs(my - ey) + 1; i++) {
          if (!(board[ex][ey + i * dir] instanceof Empty)) {
            cf = false;
            break;
          }
        }
        // println("done loop!");
      } else {
        // println("Checkking flows");
        for (int i = 0; i < abs(my - ey); i++) {
          if (!(board[ex][ey + i * dir] instanceof Flow)) {
            cf = false;
            break;
          }
        }
      }
      if (cf) {
        //print("works");
        if ((ey < my && c.y < ey) || (ey > my && c.y > ey)) {

          for (int i = 0; i < abs(my - ey); i++) {
            board[ex][ey + i * dir] = new Flow(ex, ey + i * dir, c, false);
            c.n--;
          }
          board[ex][my] = new Flow(ex, my, c, true);
          //  if(abs(my-ey) != 1) c.n--;
        } else {
          for (int i = 0; i < abs(my - ey); i++) {
            board[ex][ey + i * dir] = new Empty(ex, ey + i * dir);
            c.n++;
          }
          if (board[ex][my] instanceof Flow) {
            Flow k = (Flow) board[ex][my];
            if (k.c == c)
              board[ex][my] = new Flow(ex, my, c, true);
          }
        }
        // c.n--;
      }
    }
    eLocked = false;
  }
  
}

public void mouseClicked() {
  updateMouseCoords();
  //if (board[mx][my] instanceof Empty) {
  //  //board[mx][my] = new Circle(mx, my, floor(random(10)));
  //}
  
}
class Block{
  int x;
  int y;
  Block(int x, int y){
    this.x = x;
    this.y = y;
  }
}
class Circle{
int x;
int y;
int n;
int col;
  Circle(int x, int y, int n){
    this.x = x;
    this.y = y;
    this.n = n;
    int r = rInt(200);
    int g = rInt(200);
    int b = rInt(200);
    col = color(r+50,g+50,b+50);
  }
  public int rInt(int ran){
    return floor(random(ran));
  }
}
class Empty{
  int x;
  int y;
    Empty(int x, int y){
      this.x = x;
      this.y = y;
    }
}
class Flow{
  int x;
  int y;
  Circle c;
  int dir;
  boolean edge;
  Flow(int x, int y, Circle c, boolean edge){
    this.x = x;
    this.y = y;
    this.c = c;
    this.edge = edge;
    findDir();
  }
  
  public void findDir(){
    if(c.x == x){
      if(c.y < y){
        dir = DIR_DOWN;
      } 
      else dir = DIR_UP;
    } else {
      if(c.x > x){
        dir = DIR_LEFT;
      }
      else dir = DIR_RIGHT;
    }
  }

}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "cross_path" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
