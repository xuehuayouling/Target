package com.ruili.target.activitys;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
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

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RatingBar;
import android.widget.TextView;

public class TargetDetailsActivity extends BaseActivity implements OnClickListener {
	public static final String KEY_SUBCATEGORY = "subcategory";
	private static final int REQUEST_CODE_IMAGE_CAPTURE = 300;
	private String photoUriPath;
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
		mGlpics.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ImageView imageView = (ImageView) view;
				showImageDialog(((BitmapDrawable) imageView.getDrawable()).getBitmap());
			}
		});
		mGlpics.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
				Dialog dialog = new AlertDialog.Builder(TargetDetailsActivity.this).setTitle(R.string.delete_confirm)
						.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

					}
				}).setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						mImageAdapter.getItem(position);
						mPicPaths.remove(position);
						mImageAdapter.setPicPaths(mPicPaths);
						Logger.debug(TAG, "remove one pic -->  mPicPaths.size()" + mPicPaths.size());
					}
				}).create();
				dialog.show();
				return true;
			}
		});
	}

	private Dialog mPicDialog;

	private void showImageDialog(Bitmap bitmap) {
		if (mPicDialog == null) {
			mPicDialog = new Dialog(this, R.style.AppTheme);
			mPicDialog.setContentView(R.layout.widget_pic_full_screen);
		}
		ImageView imageView = (ImageView) mPicDialog.findViewById(R.id.imgv_photo_full_screen);
		imageView.setImageBitmap(bitmap);
		imageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mPicDialog.dismiss();
			}
		});
		mPicDialog.show();
	}

	private void initRequestQueue() {
		mQueue = Volley.newRequestQueue(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		update();
	}

	private void update() {
		if (mSubcategory == null) {
			getProgressDialogUtils().show("");
			StringRequest stringRequest = new StringRequest(Method.GET,
					getSubCategoryUrl(getIntent().getIntExtra(KEY_SUBCATEGORY, -1)), new Response.Listener<String>() {

						@Override
						public void onResponse(String response) {
							Logger.debug(TAG, "update success -->  " + response);
							getProgressDialogUtils().cancel();
							decodeResponse(response);
						}
					}, new Response.ErrorListener() {

						@Override
						public void onErrorResponse(VolleyError error) {
							Logger.debug(TAG, "update fail -->  " + error.toString());
							getProgressDialogUtils().cancel();
							getToast().show(R.string.netword_fail);
						}
					});
			mQueue.add(stringRequest);
		}
	}

	private void decodeResponse(String response) {
		Log.d(TAG, response);
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
					Logger.debug(TAG, "add one pic -->  mPicPaths.size()" + mPicPaths.size());
				}
			}
		}
	}

	private String getSubCategoryUrl(int categoryId) {
		String url = Constant.BASE_URL + String.format("/api/v1/index/%d//index_log", categoryId);
		return url;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imgv_take_photo:
			photoUriPath = Constant.BASE_IMAGE_CAPTURE_PATH + System.currentTimeMillis() + Constant.PIC_END_STR;
			Uri uri = Uri.fromFile(new File(photoUriPath));
			Intent intent = new Intent();
			intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
			startActivityForResult(intent, REQUEST_CODE_IMAGE_CAPTURE);
			break;
		case R.id.iv_back:
			finish();
			break;
		case R.id.tv_save:
			startSaveSubcategory();
			break;
		default:
			break;
		}
	}

	private List<String> mNeedUploadPicPaths;

	private void startSaveSubcategory() {
		mSubcategory.setIndex_pic(null);
		Logger.debug(TAG, "uploadImage -->  mPicPaths.size()" + mPicPaths.size());
		if (mPicPaths.size() > 0) {
			mNeedUploadPicPaths = new ArrayList<>();
			for (String path : mPicPaths) {
				if (path.startsWith(Constant.BASE_IMAGE_CAPTURE_PATH)) {
					mNeedUploadPicPaths.add(path);
				} else {
					List<PicUrl> picUrls = mSubcategory.getIndex_pic();
					if (picUrls == null) {
						picUrls = new ArrayList<>();
					}
					PicUrl picUrl = new PicUrl();
					picUrl.setPic_url(path);
					picUrls.add(picUrl);
					mSubcategory.setIndex_pic(picUrls);
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
		StringRequest stringRequest = new StringRequest(Method.PUT,
				getUpdateSubCategoryUrl(mSubcategory.getIndex_log_id()), new Response.Listener<String>() {

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
			Logger.debug(TAG, "uploadImage --> picPath: " + path);
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
					uploadImage(id + 1);
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
				mPicPaths.add(photoUriPath);
				mImageAdapter.setPicPaths(mPicPaths);
			}
		}
	}

}
