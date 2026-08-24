package waterButton;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

class MyFrame extends Frame implements ActionListener {

    //按钮数组大小
    private final int ROWS = 10;
    private final int COLS = 10;

    private MyButton btn[][] = new MyButton[ROWS][COLS];
    private int vis[][] = new int[ROWS][COLS];
    private Color a = new Color(252,157,154);//原始颜色
    private Color b = new Color(254,67,101);//点击颜色
    private int dir[][] = new int[][]{{0,1},{1,0},{0,-1},{-1,0}};

    //统一调度所有变色任务，避免每个按钮都开一个线程
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    //每个按钮当前被几个波纹覆盖，只在调度线程上读写
    private int wave[][] = new int[ROWS][COLS];

    public MyFrame() {
        //常规设置
        this.setTitle("波纹按钮效果示意");
        this.setSize(500, 500);
        this.setLayout(new GridLayout(ROWS,COLS));//网格状布局
        this.setResizable(false);//不可改变大小
        //使关闭窗口有效
        this.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
                scheduler.shutdownNow();
                MyFrame.this.dispose();
                System.exit(0);
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
        //按钮全部添加完毕、布局确定后，再居中并显示
        Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) screensize.getWidth() / 2 - this.getWidth() / 2;
        int y = (int) screensize.getHeight() / 2 - this.getHeight() / 2;
        this.setLocation(x, y);
        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //按钮自身携带数组下标，直接使用
        MyButton bt = (MyButton)e.getSource();
        goButton(bt.x, bt.y);
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
        //延时交给调度器，UI 更新回到事件派发线程(EDT)
        //time 毫秒后变色，再保持至少 50 毫秒后恢复原色
        int hold = Math.max(time, 50);
        scheduler.schedule(new Runnable() {
            public void run() {
                wave[x][y]++;
                setButtonColor(x, y, nextColor);
            }
        }, time, TimeUnit.MILLISECONDS);
        scheduler.schedule(new Runnable() {
            public void run() {
                //多个波纹叠加时，最后一个波纹离开才恢复原色
                if(--wave[x][y] == 0) {
                    setButtonColor(x, y, preColor);
                }
            }
        }, time + hold, TimeUnit.MILLISECONDS);
    }

    private void setButtonColor(final int x, final int y, final Color color) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                btn[x][y].setBackground(color);
            }
        });
    }

    public void goButton(int idx,int idy) {
        //BFS
        Queue<MyButton> q = new LinkedList<MyButton>();
        btn[idx][idy].deep = 0;//起始点第0层
        q.offer(btn[idx][idy]);
        vis[idx][idy] = 1;
        changeColor(idx, idy, a, b, 0);//起始点自身也要变色
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

    public boolean judge(int idx,int idy) {
        if(idx<0||idx>=ROWS||idy<0||idy>=COLS) return false;
        if(vis[idx][idy] == 1)return false; //用过了
        vis[idx][idy] = 1;
        return true;
    }

}