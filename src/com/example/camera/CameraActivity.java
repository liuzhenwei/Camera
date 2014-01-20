package com.example.camera;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.content.DialogInterface;  
import android.content.DialogInterface.OnClickListener;  
import android.content.Intent;
import android.widget.ImageView;  


public class CameraActivity extends Activity implements SurfaceHolder.Callback, Camera.PictureCallback{
	private static final String TAG = "CameraActivity";

    private final static int MENU_SAVE = Menu.FIRST;  
    private final static int MENU_ABOUT = Menu.FIRST + 1;  
    private final static int MENU_EXIT = Menu.FIRST + 2;  

	private SurfaceView surfaceView;
	private ImageView imgView;

	private Camera camera;

	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.camera);

		surfaceView = (SurfaceView)this.findViewById(R.id.cameraview);
		surfaceView.setFocusable(true);
		surfaceView.setFocusableInTouchMode(true);

		//SurfaceView中的getHolder方法可以获取到一个SurfaceHolder实例
		SurfaceHolder holder = surfaceView.getHolder();
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		holder.addCallback(this);

        ImageView capture = (ImageView) findViewById(R.id.img1);
        capture.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // get an image from the camera
                	camera.takePicture(null, null, null, CameraActivity.this);
                }
            }
        );
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_SAVE, 1, "保存");
        menu.add(0, MENU_ABOUT, 2, "关于");  
        menu.add(0, MENU_EXIT, 3, "退出");  
        return super.onCreateOptionsMenu(menu);  
	}
    @Override  
    public boolean onOptionsItemSelected(MenuItem item) {  
        // TODO Auto-generated method stub  
        switch (item.getItemId())  
        {  
        case MENU_SAVE:  
        	camera.takePicture(null, null, null, CameraActivity.this);
            break;  
        case MENU_ABOUT:  
            AlertDialog.Builder bdAbout = new Builder(CameraActivity.this);  
            bdAbout.setMessage("About");
            bdAbout.setTitle("关于");
            bdAbout.setPositiveButton("确认", new OnClickListener(){
                @Override  
                public void onClick(DialogInterface arg0, int arg1) {  
                    // TODO Auto-generated method stub  
                    arg0.dismiss();  
                }  
            });  
            bdAbout.create().show();  
            break;  
        case MENU_EXIT:  
            AlertDialog.Builder bdExit = new Builder(CameraActivity.this);  
            bdExit.setMessage("确认退出吗？");  
            bdExit.setTitle("提示");  
            bdExit.setPositiveButton("确认", new OnClickListener(){  
                @Override  
                public void onClick(DialogInterface arg0, int arg1) {  
                    // TODO Auto-generated method stub  
                    arg0.dismiss();  
                    exit();  
                }  
            });  
            bdExit.setNegativeButton("取消", new OnClickListener(){  
                @Override  
                public void onClick(DialogInterface dialog, int which) {  
                    // TODO Auto-generated method stub  
                    dialog.dismiss();  
                }  
            });  
            bdExit.create().show();  
            break;  
        default:  
            break;  
        }  
        return super.onOptionsItemSelected(item);  
    }  

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {

		Camera.Parameters param = camera.getParameters();
		if(this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE){
			//如果是竖屏
			// param.set("orientation", "portrait");
			//在2.2以上可以使用
			camera.setDisplayOrientation(90);
		}else{
			//param.set("orientation", "landscape");
			//在2.2以上可以使用
			camera.setDisplayOrientation(0);
		}

        int bestWidth = 800; 
        int bestHeight = 600;
        int width = 0;
        int height = 0;         
        List<Camera.Size> sizeList = param.getSupportedPictureSizes(); 
        //如果sizeList只有一个我们也没有必要做什么了，因为就他一个别无选择 
        if(sizeList.size() > 1){ 
            Iterator<Camera.Size> itor = sizeList.iterator(); 
            while(itor.hasNext()){ 
                Camera.Size cur = itor.next();
                if( cur.width > width && cur.width <= bestWidth){ 
                    width = cur.width;
                }
                if(cur.height > height && cur.height <= bestHeight){ 
                	height = cur.height; 
                }
            } 
        }
		param.setPictureSize(width, height);
		
		//设置完成需要再次调用setParameter方法才能生效
		camera.setParameters(param);

		//启动预览功能
		camera.startPreview();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// 当Surface被创建的时候，该方法被调用，可以在这里实例化Camera对象
		
		camera = Camera.open(0); //获取Camera实例，2.2版以上open必须加参数
		try {
			camera.setPreviewDisplay(holder);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// 当Surface被销毁的时候，该方法被调用
		//在这里需要释放Camera资源
		camera.stopPreview();
		camera.release();

	}

	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
		// data是一个原始的JPEG图像数据，
		// 注意保存图片后，再次调用startPreview()回到预览
		OutputStream fos = null;
		try {
            Date mDate = new Date(System.currentTimeMillis());  
            SimpleDateFormat mFmt = new SimpleDateFormat("MMddhhmmss");  
            String mFileName = mFmt.format(mDate);  
            Bitmap jpg = BitmapFactory.decodeByteArray(data, 0, data.length);  
            File fPath = new File(Environment.getExternalStorageDirectory().toString()+"/Download/");
            if (!fPath.exists())  
            {  
                fPath.mkdir();  
            }  
            File fName = new File(fPath, mFileName+".jpg");  
            if (!fName.exists())  
            {  
                fName.createNewFile();  
            }  
            fos = new FileOutputStream(fName);  
            jpg.compress(Bitmap.CompressFormat.JPEG, 80, fos);

		} catch (FileNotFoundException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        } catch (IOException e) {  
            // TODO Auto-generated catch block  
        	e.printStackTrace();  
        }  
        finally  
        {  
            try {
           		fos.flush();  
           		fos.close();  
            } catch (IOException e) {  
                // TODO Auto-generated catch block  
                e.printStackTrace();  
            }  
        }  

		camera.startPreview();
	}

    
	private void exit()  
	{  
		Intent startMain = new Intent(Intent.ACTION_MAIN);
		startMain.addCategory(Intent.CATEGORY_HOME);
		startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(startMain);
		System.exit(0);
	}  
}