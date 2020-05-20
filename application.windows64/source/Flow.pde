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
  
  void findDir(){
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
