import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

public class mazesolver extends JFrame {
private int [][] maze = {
    {1,1,1,1,1,1,1,1,1,1,1,1,1},        
    {1,0,1,0,1,0,1,0,0,0,0,0,1},        
    {1,0,1,0,0,0,1,0,1,1,1,0,1},
    {1,0,1,1,1,1,1,0,0,0,0,0,1},
    {1,0,0,1,0,0,0,0,1,1,1,0,1},
    {1,0,1,0,1,1,1,0,1,0,0,0,1},
    {1,0,1,0,1,0,0,0,1,1,1,0,1},
    {1,0,1,0,1,1,1,0,1,0,1,0,1},
    {1,0,0,0,0,0,0,0,0,0,1,9,1},
    {1,1,1,1,1,1,1,1,1,1,1,1,1},
};
    public List <Integer>path = new ArrayList<>();
 
    public mazesolver(){
        setTitle("Route Navigator");
        setSize(640, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        depthsearch.searchpath(maze, 1, 1, path);
        //System.out.println(path);
    }
       
    

    @Override
    public void paint(Graphics g){
        g.translate(50, 50);
        super.paint(g);
      
        for(int i=0;i<maze.length;i++){
            for(int j=0;j<maze[0].length;j++){
                Color color;
                switch(maze[i][j]){
                    case 1: 
                    color=Color.BLACK;
                    break;
                    case 9:
                    color = Color.RED; 
                    break;
                    default:
                    color = Color.WHITE; 
                    break;
                }
                g.setColor(color);
                g.fillRect(j*40, i*40, 40, 40);
                // g.setColor(Color.RED);
                // g.drawRect(j*40, i*40, 40, 40);
            }                   
        }
       
      
        for(int i=0;i<path.size();i+=2){
            int pathx=path.get(i);
            int pathy=path.get(i+1);
            g.setColor(Color.blue);
            g.fillRect(40*pathx, 40*pathy, 40, 40);
          
        }        
    }
      
    public static void main(String[] args) {
        mazesolver view = new mazesolver();
        view.setVisible(true);
    }  
}