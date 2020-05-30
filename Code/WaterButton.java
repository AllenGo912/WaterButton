package waterButton;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.LinkedList;
import java.util.Queue;

class MyLayout extends Frame implements ActionListener{
	
	//��ť�����С
	private int ROWS = 10;
	private int COLS = 10;
	
	class MyButton extends Button{
		public int x,y;//��ť�������е��±�
		public int deep;//��ť�Ĳ��
	}
	
	private MyButton btn[][] = new MyButton[ROWS][COLS];
	private int vis[][] = new int[ROWS][COLS];
	private Color a = new Color(252,157,154);//ԭʼ��ɫ
	private Color b = new Color(254,67,101);//�����ɫ
	private int dir[][] = new int[][]{{0,1},{1,0},{0,-1},{-1,0}};
	public MyLayout() {
		//��������
		this.setTitle("���ư�ťЧ��ʾ��");
		this.setSize(500, 500);
		this.setLayout(new GridLayout(ROWS,COLS));//����״����
		this.setResizable(false);//���ɸı��С
		//���ô�������Ļ����
		Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) screensize.getWidth() / 2 - this.getWidth() / 2;
		int y = (int) screensize.getHeight() / 2 - this.getHeight() / 2;	
		this.setLocation(x, y);
		//���ô��ڿɼ�
		this.setVisible(true);
		//ʹ�رմ�����Ч
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
				btn[i][j].addActionListener(this);//ע�����
				this.add(btn[i][j]);
			}
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		//��ȡ��ť�����е����ť�±�
		//ͨ������ͳ������
		MyButton bt = (MyButton)e.getSource();
		int idx = bt.getY()/bt.getHeight();
		int idy = bt.getX()/bt.getWidth();
		goButton(idx, idy);
		init();
	}
	
	public void init(){
		for(int i=0;i<ROWS;i++){
			for(int j=0;j<COLS;j++){
				vis[i][j] = 0; //δ���ʹ�
			}
		}
	}
	public void changeColor(int x,int y,Color preColor,Color nextColor,int time) {
		//btn[x][y].setBackground(preColor);
		//��ʱ
		new Thread() {
            public void run() {
            	btn[x][y].setBackground(preColor);
            	try {
//            		System.out.println("��ʱ!+"+time+"end");
            		Thread.sleep(time);
                 } catch (InterruptedException e) {
                	 e.printStackTrace();
                 }
            	btn[x][y].setBackground(nextColor);
            	try {
//            		System.out.println("��ʱ!+"+time+"end");
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
		btn[idx][idy].deep = 0;//��ʼ���0��
		q.offer(btn[idx][idy]);
		vis[idx][idy] = 1;
//		System.out.println(idx+","+idy+"������~");
		while(!q.isEmpty()) {
			MyButton head = q.poll();
			for(int i=0;i<4;i++) {
				int tx = head.x + dir[i][0];
				int ty = head.y + dir[i][1];
				if(judge(tx,ty)) {
					btn[tx][ty].deep = head.deep + 1;
					changeColor(tx,ty,a, b, btn[tx][ty].deep*50);
					q.offer(btn[tx][ty]);
//					System.out.println("�ı���ɫ"+tx+','+ty+",   deep:"+(head.deep+1));
				}
			}
		}
//		System.out.println("size" + q.size());
	}
	
	public Boolean judge(int idx,int idy) {
		if(idx<0||idx>=ROWS||idy<0||idy>=COLS) return false;
		if(vis[idx][idy] == 1)return false; //�ù���
		vis[idx][idy] = 1;
		return true;
	}
	
}
public class WaterButton {
	public static void main(String[] args) throws Exception {
		/**
		 *	 ʵ�ֵ�Ч��
		 *	���һ����ť��Ȼ������������Ӧ����ɫ������ˮ�Ĳ���
		 */
		new MyLayout();
	}
	
}
