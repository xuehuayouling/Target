package com.ruili.target.activitys;

import com.ruili.target.R;
import com.ruili.target.entity.Subcategory;

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
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;

public class TargetDetailsActivity extends Activity implements OnClickListener {
	public static final String KEY_SUBCATEGORY = "subcategory";
	private static final int REQUEST_CODE_IMAGE_CAPTURE = 300;
	private Uri photoUri;
	private Subcategory mSubcategory;
	private TextView mTVTitle;
	private RadioGroup mRGState;
	private RatingBar mRBarScore;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_target_details);
		ImageView imgVTakePhoto = (ImageView) findViewById(R.id.imgv_take_photo);
		imgVTakePhoto.setOnClickListener(this);
		mTVTitle = (TextView) findViewById(R.id.tv_title);
		mRGState = (RadioGroup) findViewById(R.id.rg_state);
		mRBarScore = (RatingBar) findViewById(R.id.rbar_score);
		ImageView ivBack = (ImageView) findViewById(R.id.iv_back);
		ivBack.setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mSubcategory = (Subcategory) getIntent().getSerializableExtra(KEY_SUBCATEGORY);
		mTVTitle.setText(mSubcategory.getSmall_index_name());
		if (mSubcategory.getIndex_type() == Subcategory.INDEX_TYPE_SCORE) {
			mRGState.setVisibility(View.GONE);
			mRBarScore.setVisibility(View.VISIBLE);
			mRBarScore.setRating(Integer.valueOf(mSubcategory.getIndex_score()));
		} else {
			mRGState.setVisibility(View.VISIBLE);
			mRBarScore.setVisibility(View.GONE);
			if (mSubcategory.getIndex_complete() == Subcategory.INDEX_COMPLETE_YES) {
				mRGState.check(R.id.rbtn_yes);
			} else if (mSubcategory.getIndex_complete() == Subcategory.INDEX_COMPLETE_NO) {
				mRGState.check(R.id.rbtn_no);
			} else {
				mRGState.clearCheck();
			}
		}
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
			startActivityForResult(intent, REQUEST_CODE_IMAGE_CAPTURE);
			break;
		case R.id.iv_back:
			finish();
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE_IMAGE_CAPTURE) {
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
