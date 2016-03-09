package com.ruili.target.activitys;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ruili.target.R;
import com.ruili.target.adapters.ImageAdapter;
import com.ruili.target.entity.PicUrl;
import com.ruili.target.entity.Subcategory;
import com.ruili.target.entity.SubcategoryDTO;
import com.ruili.target.utils.Constant;
import com.ruili.target.utils.IQiniuUploadUitlsListener;
import com.ruili.target.utils.ImageTool;
import com.ruili.target.utils.JsonUtil;
import com.ruili.target.utils.Logger;
import com.ruili.target.utils.QiniuUploadUitls;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class TargetDetailsActivity extends BaseActivity implements OnClickListener {
	public static final String KEY_SUBCATEGORY = "subcategory";
	private static final int REQUEST_CODE_IMAGE_CAPTURE = 300;
	private Uri photoUri;
	private Subcategory mSubcategory;
	private TextView mTVTitle;
	private RadioGroup mRGState;
	private RatingBar mRBarScore;
	private EditText mETRemark;
	private Gallery mGlpics;
	private LayoutInflater mInflater;
	private ImageAdapter mImageAdapter;
	private List<String> mPicPaths = new ArrayList<>();
	protected static final String TAG = TargetDetailsActivity.class.getSimpleName();
	private RequestQueue mQueue;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_target_details);
		initRequestQueue();
		mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ImageView imgVTakePhoto = (ImageView) findViewById(R.id.imgv_take_photo);
		imgVTakePhoto.setOnClickListener(this);
		TextView tvSave = (TextView) findViewById(R.id.tv_save);
		tvSave.setOnClickListener(this);
		mTVTitle = (TextView) findViewById(R.id.tv_title);
		mRGState = (RadioGroup) findViewById(R.id.rg_state);
		mRBarScore = (RatingBar) findViewById(R.id.rbar_score);
		mETRemark = (EditText) findViewById(R.id.et_remark);
		ImageView ivBack = (ImageView) findViewById(R.id.iv_back);
		ivBack.setOnClickListener(this);
		mGlpics = (Gallery) findViewById(R.id.ll_pics);
		mImageAdapter = new ImageAdapter(this, null);
		mPicPaths.clear();
		mGlpics.setAdapter(mImageAdapter);
	}
	
	private void initRequestQueue() {
		mQueue = Volley.newRequestQueue(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (mSubcategory == null) {
			getProgressDialogUtils().show("");
			StringRequest stringRequest = new StringRequest(Method.GET, getSubCategoryUrl(getIntent().getIntExtra(KEY_SUBCATEGORY, -1)),
					new Response.Listener<String>() {

						@Override
						public void onResponse(String response) {
							getProgressDialogUtils().cancel();
							decodeResponse(response);
						}
					}, new Response.ErrorListener() {

						@Override
						public void onErrorResponse(VolleyError error) {
							getProgressDialogUtils().cancel();
							getToast().show(R.string.netword_fail);
						}
					});
			mQueue.add(stringRequest);
		}
	}
	
	private void decodeResponse(String response) {
		Log.d(TAG  , response);
		try {
			SubcategoryDTO dto = JsonUtil.parseObject(response, SubcategoryDTO.class);
			if (dto.isValid()) {
				mSubcategory = dto.getData();
				refreshView();
			} else {
				getToast().show(R.string.get_data_fail);
			}
		} catch (Exception e) {
			getToast().show(getResources().getText(R.string.service_fail) + response);
			e.printStackTrace();
		}
	}

	private void refreshView() {
		if (mSubcategory != null) {
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
				mRGState.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					
					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						switch (checkedId) {
						case R.id.rbtn_yes:
							mSubcategory.setIndex_complete(Subcategory.INDEX_COMPLETE_YES);
							break;
						case R.id.rbtn_no:
							mSubcategory.setIndex_complete(Subcategory.INDEX_COMPLETE_NO);
							break;
						default:
							break;
						}
					}
				});
			}
			mETRemark.setText(mSubcategory.getIndex_remark());
			final List<PicUrl> picUrls = mSubcategory.getIndex_pic();
			if (picUrls != null) {
				for (PicUrl picUrl : picUrls) {
					mPicPaths.add(picUrl.getPic_url());
					mImageAdapter.setPicPaths(mPicPaths);
					
				}
			}
		}
	}
	
	private String getSubCategoryUrl(int categoryId) {
		String url = Constant.BASE_URL + String.format("/api/v1/index/%d//index_log",
				categoryId);
		return url;
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imgv_take_photo:
			ContentValues values = new ContentValues();
			while (photoUri == null) {
				photoUri = this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
			}
			Intent intent = new Intent();
			intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
			startActivityForResult(intent, REQUEST_CODE_IMAGE_CAPTURE);
			break;
		case R.id.iv_back:
			finish();
			break;
		case R.id.tv_save:
			uploadImage();
			break;
		default:
			break;
		}
	}

	private List<String> mNeedUploadPicPaths;
	private void uploadImage() {
		if (mPicPaths.size() > 0) {
			mNeedUploadPicPaths = new ArrayList<>();
			for (String path : mPicPaths) {
				if (path.startsWith(Constant.CAMERA_FILE_PATH)) {
					mNeedUploadPicPaths.add(path);
				}
			}
			uploadImage(0);
		} else {
			save();
		}
	}
	
	private void save() {
		if (mSubcategory.getIndex_type() == Subcategory.INDEX_TYPE_SCORE) {
			mSubcategory.setIndex_score(String.valueOf((int) mRBarScore.getRating()));
		}
		mSubcategory.setIndex_remark(mETRemark.getText().toString());
		getProgressDialogUtils().show();
		StringRequest stringRequest = new StringRequest(Method.PUT, getUpdateSubCategoryUrl(mSubcategory.getIndex_log_id()),
				new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						getProgressDialogUtils().cancel();
						Logger.debug(TAG, "save success -->  " + response);
						finish();
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						Logger.debug(TAG, "save fail -->  " + error.toString());
						getProgressDialogUtils().cancel();
						getToast().show(R.string.netword_fail);
					}
				}) {

					@Override
					protected Map<String, String> getParams() throws AuthFailureError {
						return mSubcategory.getUpdataParams(TAG);
					}
			
		};
		mQueue.add(stringRequest);
	}
	
	private String getUpdateSubCategoryUrl(int indexLogId) {
		return Constant.BASE_URL + String.format("/api/v1/index/%d/index_log", indexLogId);
	}


	private void uploadImage(final int id) {
		if (mNeedUploadPicPaths.size() > id) {
			String path = mNeedUploadPicPaths.get(id);
			getProgressDialogUtils().show();
			QiniuUploadUitls.getInstance().uploadImage(getBitmap(path), new IQiniuUploadUitlsListener() {
				
				@Override
				public void onSucess(String fileUrl) {
					Logger.debug(TAG, fileUrl);
					getProgressDialogUtils().cancel();
					List<PicUrl> picUrls = mSubcategory.getIndex_pic();
					if (picUrls == null) {
						picUrls = new ArrayList<>();
					}
					PicUrl picUrl = new PicUrl();
					picUrl.setPic_url(fileUrl);
					picUrls.add(picUrl);
					mSubcategory.setIndex_pic(picUrls);
					uploadImage(id+1);
				}
				
				@Override
				public void onProgress(int progress) {
					
				}
				
				@Override
				public void onError(int errorCode, String msg) {
					getProgressDialogUtils().cancel();
					getToast().show(getString(R.string.netword_fail) + msg);
				}
			});
		} else {
			save();
		}
	}
	
	private Bitmap getBitmap(String picPath) {
		return ImageTool.compressBitmap(picPath, 200, 150);
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
						mPicPaths.add(picPath);
						mImageAdapter.setPicPaths(mPicPaths);
					}
					cursor.close();
				}
			}
		}
	}

}
