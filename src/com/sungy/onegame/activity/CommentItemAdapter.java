package com.sungy.onegame.activity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sungy.onegame.R;
import com.sungy.onegame.mclass.DownLoadUtils;

public class CommentItemAdapter extends BaseAdapter{
	private final static String TAG = "CommentItemAdapter";
	
	private LayoutInflater inflater;
	private ViewHolder holder;
	private Context mContext;
	private Map<Integer,Bitmap> bitmaps;

	CommentItemAdapter(Context c){
		mContext = c;
		inflater = LayoutInflater.from(c);
		bitmaps = new HashMap<Integer,Bitmap>();
	}
	
	private Handler myHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			if(msg.what == 1){
				holder.comment_image.setImageBitmap(bitmaps.get(msg.arg1));
//				notifyDataSetChanged();
//				notifyDataSetInvalidated();
//				Log.e("getView","notifyDataSetInvalidated");
			}
			super.handleMessage(msg);
		}
		
	};
	

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return DetailActivity.commentList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return DetailActivity.commentList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		boolean myPosition = false;
		if(position == -1){	//为获取高度而特设的
			myPosition = true;
			position = 0;
		}
		if(convertView==null){
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.comment_item2, null);
			
			holder.comment_user = (TextView)convertView.findViewById(R.id.comment_username);
			holder.comment_time = (TextView)convertView.findViewById(R.id.comment_time);
			holder.comment_content = (TextView)convertView.findViewById(R.id.comment_content);		
			holder.comment_image = (ImageView)convertView.findViewById(R.id.comment_userimage);		
			
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		
		//时间 
		String time = DetailActivity.commentList.get(position).getComment_time();
		//去掉秒数
		time = time.substring(0,time.lastIndexOf(":"));
		holder.comment_user.setText(DetailActivity.commentList.get(position).getUser_name());
		holder.comment_time.setText(time);
		holder.comment_content.setText(DetailActivity.commentList.get(position).getComment());
		final int finalPosition = position;
		final int finalUserid = DetailActivity.commentList.get(position).getUser_id();
		if(myPosition){
			holder.comment_image.setImageResource( R.drawable.defaultimage);
		}else{
			
			if(!bitmaps.containsKey(finalUserid)){
//				Bitmap bitmap = DownLoadUtils.downloadIcon(DetailActivity.commentList.get(position).getUser_image());
//				if(bitmap==null){
//					bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.defaultimage);
//				}
//				bitmaps.put(position, bitmap);
				
				Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.defaultimage);
				bitmaps.put(finalUserid, bitmap);
				holder.comment_image.setImageBitmap(bitmaps.get(finalUserid));
				Thread t1 = new Thread(new Runnable() {
					
					@Override
					public void run() {
						Bitmap bitmap = DownLoadUtils.downloadIcon(DetailActivity.commentList.get(finalPosition).getUser_image());
						if(bitmap!=null){
							if(bitmaps.containsKey(finalUserid)){
								bitmaps.remove(finalUserid);
								bitmaps.put(finalUserid, bitmap);
								Message msg = new Message();
								msg.what = 1;
								msg.arg1 = finalUserid;
								myHandler.sendMessage(msg);
							}
						}
					}
				});
				t1.start();
			}else{
				holder.comment_image.setImageBitmap(bitmaps.get(finalUserid));
			}
		}
		return convertView;
	}
	
	public int getTextHeight(int index,int width){
		holder.comment_content.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
										MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		int textWidth = holder.comment_content.getMeasuredWidth();
//		int textSize = (int) holder.comment_content.getTextSize();
		int lineHeight = holder.comment_content.getLineHeight();
		int textHeight = holder.comment_content.getMeasuredHeight();
		LinearLayout.LayoutParams margin = (LinearLayout.LayoutParams) holder.comment_content.getLayoutParams();
		int marginW = margin.leftMargin;
//		Log.e(TAG, textWidth+"   "+width+"   "+marginW+"   ");
		//计算行数
		int nums = (textWidth%(width+marginW)==0)?(textWidth/(width+marginW)):(textWidth/(width+marginW))+1;
		//计算高度
		int h = (nums-1)*lineHeight;
		return h;
	}
	
	public void addNewComment(){
		bitmaps.clear();
	}
	
	
	class ViewHolder{
		TextView comment_user,comment_time,comment_content;
		ImageView comment_image;
	}
	
	public void destory(){
		Iterator<Integer> it = bitmaps.keySet().iterator();
		Bitmap bitmap = null;
		while(it.hasNext()){
			bitmap = bitmaps.get(it.next());
			bitmap = null;
		}
		if(bitmaps != null &&	bitmaps.size() !=0){
			bitmaps.clear();
		}
	}

}

