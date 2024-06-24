package waterButton;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.LinkedList;
import java.util.Queue;

class MyLayout extends Frame implements ActionListener{

	//按钮数组大小
	private final int ROWS = 10;
	private final int COLS = 10;

	class MyButton extends Button{
		public int x,y;//按钮在数组中的下标
		public int deep;//按钮的层次
	}

	private MyButton btn[][] = new MyButton[ROWS][COLS];
	private int vis[][] = new int[ROWS][COLS];
	private Color a = new Color(252,157,154);//原始颜色
	private Color b = new Color(254,67,101);//点击颜色
	private int dir[][] = new int[][]{{0,1},{1,0},{0,-1},{-1,0}};
	public MyLayout() {
		//常规设置
		this.setTitle("波纹按钮效果示意");
		this.setSize(500, 500);
		this.setLayout(new GridLayout(ROWS,COLS));//网格状布局
		this.setResizable(false);//不可改变大小
		//设置窗口在屏幕中央
		Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) screensize.getWidth() / 2 - this.getWidth() / 2;
		int y = (int) screensize.getHeight() / 2 - this.getHeight() / 2;
		this.setLocation(x, y);
		//设置窗口可见
		this.setVisible(true);
		//使关闭窗口有效
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				MyLayout.this.dispose();
			}
		});

		for(int i=0;i<ROWS;i++) {
			for(int j=0;j<COLS;j++) {
				btn[i][j] = new MyButton();
				btn[i][j].setBackground(a);
				btn[i][j].x = i;
				btn[i][j].y = j;
				btn[i][j].addActionListener(this);//注册监听
				this.add(btn[i][j]);
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		//获取按钮数组中点击按钮下标
		//通过坐标和长宽计算
		MyButton bt = (MyButton)e.getSource();
		int idx = bt.getY()/bt.getHeight();
		int idy = bt.getX()/bt.getWidth();
		goButton(idx, idy);
		init();
	}

	public void init(){
		for(int i=0;i<ROWS;i++){
			for(int j=0;j<COLS;j++){
				vis[i][j] = 0; //未访问过
			}
		}
	}
	public void changeColor(int x,int y,Color preColor,Color nextColor,int time) {
		//btn[x][y].setBackground(preColor);
		//延时
		new Thread() {
			public void run() {
				btn[x][y].setBackground(preColor);
				try {
//            		System.out.println("延时!+"+time+"end");
					Thread.sleep(time);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				btn[x][y].setBackground(nextColor);
				try {
//            		System.out.println("延时!+"+time+"end");
					Thread.sleep(time);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				btn[x][y].setBackground(preColor);
			}
		}.start();
	}

	public void goButton(int idx,int idy) {
		//BFS
		Queue<MyButton> q = new LinkedList<MyButton>();
		btn[idx][idy].deep = 0;//起始点第0层
		q.offer(btn[idx][idy]);
		vis[idx][idy] = 1;
//		System.out.println(idx+","+idy+"被调用~");
		while(!q.isEmpty()) {
			MyButton head = q.poll();
			for(int i=0;i<4;i++) {
				int tx = head.x + dir[i][0];
				int ty = head.y + dir[i][1];
				if(judge(tx,ty)) {
					btn[tx][ty].deep = head.deep + 1;
					changeColor(tx,ty,a, b, btn[tx][ty].deep*50);
					q.offer(btn[tx][ty]);
//					System.out.println("改变颜色"+tx+','+ty+",   deep:"+(head.deep+1));
				}
			}
		}
//		System.out.println("size" + q.size());
	}

	public Boolean judge(int idx,int idy) {
		if(idx<0||idx>=ROWS||idy<0||idy>=COLS) return false;
		if(vis[idx][idy] == 1)return false; //用过了
		vis[idx][idy] = 1;
		return true;
	}

}
public class WaterButton {
	public static void main(String[] args) throws Exception {
		/**
		 *	 实现的效果
		 *	点击一个按钮，然后引发连锁反应变颜色，类似水的波纹 采用BFS思想
		 */
		// 自己修改
		new MyLayout();
	}

}