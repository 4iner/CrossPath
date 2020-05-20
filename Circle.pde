class Circle{
int x;
int y;
int n;
color col;
  Circle(int x, int y, int n){
    this.x = x;
    this.y = y;
    this.n = n;
    int r = rInt(200);
    int g = rInt(200);
    int b = rInt(200);
    col = color(r+50,g+50,b+50);
  }
  int rInt(int ran){
    return floor(random(ran));
  }
}
