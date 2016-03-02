package com.ruili.target.activitys;

import com.ruili.target.R;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class TargetDetailsActivity extends Activity implements OnClickListener {
	private Uri photoUri;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		setContentView(R.layout.activity_target_details);
		ImageView imgVTakePhoto = (ImageView) findViewById(R.id.imgv_take_photo);
		imgVTakePhoto.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imgv_take_photo:
			Intent intent = new Intent();
			intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
			ContentValues values = new ContentValues();
			photoUri = this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
			startActivityForResult(intent, 300);
			break;

		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 300) {
			if (resultCode == RESULT_OK) {
				String[] pojo = { MediaStore.Images.Media.DATA };
				Cursor cursor = getContentResolver().query(photoUri, pojo, null, null, null);
				if (cursor != null) {
					int columnIndex = cursor.getColumnIndexOrThrow(pojo[0]);
					if (cursor.moveToFirst()) {
						String picPath = cursor.getString(columnIndex);
						Bitmap bmap = BitmapFactory.decodeFile(picPath);
						ImageView imageview = (ImageView) this.findViewById(R.id.imgv_photo_preview);
						imageview.setImageBitmap(bmap);
					}
					cursor.close();
				}
			}
		}
	}

}
