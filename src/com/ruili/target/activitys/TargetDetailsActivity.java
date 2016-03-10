package com.ruili.target.activitys;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
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
import com.ruili.target.utils.IQiniuUploadManagerListener;
import com.ruili.target.utils.ImageTool;
import com.ruili.target.utils.JsonUtil;
import com.ruili.target.utils.Logger;
import com.ruili.target.utils.QiniuUploadManager;
import com.ruili.target.utils.TextUtil;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RatingBar;
import android.widget.TextView;

public class TargetDetailsActivity extends BaseActivity implements OnClickListener {
	public static final String KEY_SUBCATEGORY = "subcategory";
	public static final String KEY_TYPE = "type";
	private static final int REQUEST_CODE_IMAGE_CAPTURE = 300;
	private String photoUriPath;
	private Subcategory mSubcategory;
	private TextView mTVTitle;
	private RadioGroup mRGState;
	private RatingBar mRBarScore;
	private EditText mETRemark;
	private TextView mTVRemark;
	private EditText mETComment;
	private Gallery mGlpics;
	private LayoutInflater mInflater;
	private ImageAdapter mImageAdapter;
	private List<String> mPicPaths = new ArrayList<>();
	protected static final String TAG = TargetDetailsActivity.class.getSimpleName();
	private RequestQueue mQueue;
	// 今日指标、历史指标、监督管理
	private int mType;
	
	private static final int SATISFACTION_GOOD = 0;
	private static final int SATISFACTION_NORMAL = 1;
	private static final int SATISFACTION_BAD = 2;
	private int mSatisfaction = -1;
	private List<String> mNeedUploadPicPaths;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_target_details);
		initRequestQueue();
		mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		initView();
	}

	private void initView() {
		mType = getIntent().getIntExtra(KEY_TYPE, -1);
		ImageView imgVTakePhoto = (ImageView) findViewById(R.id.imgv_take_photo);
		imgVTakePhoto.setOnClickListener(this);
		TextView tvSave = (TextView) findViewById(R.id.tv_save);
		tvSave.setOnClickListener(this);
		mTVTitle = (TextView) findViewById(R.id.tv_title);
		mRGState = (RadioGroup) findViewById(R.id.rg_state);
		mRBarScore = (RatingBar) findViewById(R.id.rbar_score);
		mETRemark = (EditText) findViewById(R.id.et_remark);
		mTVRemark = (TextView) findViewById(R.id.tv_remark);
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
				if (mType == TargetListActivity.TYPE_INSPECT_SUPERVISE) {
					return false;
				} else {
					Dialog dialog = new AlertDialog.Builder(TargetDetailsActivity.this)
							.setTitle(R.string.delete_confirm)
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
			}
		});
		Button btnSatisfactionGood = (Button) findViewById(R.id.btn_satisfaction_good);
		Button btnSatisfactionNormal = (Button) findViewById(R.id.btn_satisfaction_normal);
		Button btnSatisfactionBad = (Button) findViewById(R.id.btn_satisfaction_bad);
		btnSatisfactionGood.setOnClickListener(this);
		btnSatisfactionNormal.setOnClickListener(this);
		btnSatisfactionBad.setOnClickListener(this);
		mETComment = (EditText) findViewById(R.id.et_comment);
		if (mType == TargetListActivity.TYPE_INSPECT_SUPERVISE) {
			for (int i = 0; i < mRGState.getChildCount(); i++) {
				mRGState.getChildAt(i).setEnabled(false);
			}
			mRBarScore.setEnabled(false);
			mTVRemark.setVisibility(View.VISIBLE);
			mETRemark.setVisibility(View.GONE);
			imgVTakePhoto.setVisibility(View.GONE);
		}
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
			mTVRemark.setText(mSubcategory.getIndex_remark());
			mETComment.setText(mSubcategory.getQc_describe());
			try {
				int qc_state = Integer.valueOf(mSubcategory.getQc_state());
				updateSatisfactionBtn(qc_state);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
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
		Logger.debug(TAG, "getSubCategoryUrl -->  " + url);
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
		case R.id.btn_satisfaction_good:
		case R.id.btn_satisfaction_normal:
		case R.id.btn_satisfaction_bad:
			changeSatisfaction(v.getId());
			break;
		default:
			break;
		}
	}

	private void changeSatisfaction(int satisfactionId) {
		findViewById(R.id.btn_satisfaction_good).setBackgroundResource(android.R.color.darker_gray);
		findViewById(R.id.btn_satisfaction_normal).setBackgroundResource(android.R.color.darker_gray);
		findViewById(R.id.btn_satisfaction_bad).setBackgroundResource(android.R.color.darker_gray);
		switch (satisfactionId) {
		case R.id.btn_satisfaction_good:
			mSatisfaction = SATISFACTION_GOOD;
			findViewById(R.id.btn_satisfaction_good).setBackgroundResource(R.color.blue);
			break;
		case R.id.btn_satisfaction_normal:
			mSatisfaction = SATISFACTION_NORMAL;
			findViewById(R.id.btn_satisfaction_normal).setBackgroundResource(R.color.blue);
			break;
		case R.id.btn_satisfaction_bad:
			mSatisfaction = SATISFACTION_BAD;
			findViewById(R.id.btn_satisfaction_bad).setBackgroundResource(R.color.blue);
			break;

		default:
			break;
		}
	}
	
	private void updateSatisfactionBtn(int state) {
		switch (state) {
		case SATISFACTION_GOOD:
			mSatisfaction = state;
			findViewById(R.id.btn_satisfaction_good).setBackgroundResource(R.color.blue);
			break;
		case SATISFACTION_NORMAL:
			mSatisfaction = state;
			findViewById(R.id.btn_satisfaction_normal).setBackgroundResource(R.color.blue);
			break;
		case SATISFACTION_BAD:
			mSatisfaction = state;
			findViewById(R.id.btn_satisfaction_bad).setBackgroundResource(R.color.blue);
			break;
		default:
			break;
		}
	}

	private void startSaveSubcategory() {
		if (mType == TargetListActivity.TYPE_INSPECT_SUPERVISE) {
			startSaveSubcategoryBySuperintendent();
		} else {
			startSaveSubcategoryByDirector();
		}
	}
	private void startSaveSubcategoryBySuperintendent() {
		getProgressDialogUtils().show();
		StringRequest stringRequest = new StringRequest(Method.PUT,
				getUpdateSubCategoryUrlBySuperintendent(mSubcategory.getIndex_log_id()), new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						getProgressDialogUtils().cancel();
						Logger.debug(TAG, "startSaveSubcategoryBySuperintendent success -->  " + response);
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
				Map<String, String> map = new HashMap<String, String>();
				map.put("qc_state", String.valueOf(mSatisfaction));
				map.put("qc_describe", mETComment.getText().toString());
				map.put("qc_id", String.valueOf(getUserOperatorID()));
				return map;
			}

		};
		mQueue.add(stringRequest);
	}

	private void startSaveSubcategoryByDirector() {
		getProgressDialogUtils().show();
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
			if (!mNeedUploadPicPaths.isEmpty()) {
				final List<Map<String, String>> files = new ArrayList<>();
				for (String path : mNeedUploadPicPaths) {
					Map<String, String> map = new HashMap<>();
					map.put(QiniuUploadManager.KEY_FILE_PATH, path);
					map.put(QiniuUploadManager.KEY_SAVE_NAME, path.replace(Constant.BASE_IMAGE_CAPTURE_PATH, ""));
					files.add(map);
				}
				QiniuUploadManager manager = new QiniuUploadManager();
				manager.multipleUpload(files, new IQiniuUploadManagerListener() {

					@Override
					public void onMultipleUploadFail(String reason) {
						getProgressDialogUtils().cancel();
						getToast().show(getString(R.string.netword_fail) + reason);
					}

					@Override
					public void onMultipleUploadDone() {
						List<PicUrl> picUrls = mSubcategory.getIndex_pic();
						if (picUrls == null) {
							picUrls = new ArrayList<>();
						}
						for (Map<String, String> map : files) {
							PicUrl picUrl = new PicUrl();
							picUrl.setPic_url(
									QiniuUploadManager.getFileHttpUrlByName(map.get(QiniuUploadManager.KEY_SAVE_NAME)));
							picUrls.add(picUrl);
						}
						mSubcategory.setIndex_pic(picUrls);
						save();
					}
				});
			} else {
				save();
			}
		} else {
			save();
		}
	}

	private void save() {
		if (mSubcategory.getIndex_type() == Subcategory.INDEX_TYPE_SCORE) {
			mSubcategory.setIndex_score(String.valueOf((int) mRBarScore.getRating()));
		}
		mSubcategory.setIndex_remark(mETRemark.getText().toString());
		StringRequest stringRequest = new StringRequest(Method.PUT,
				getUpdateSubCategoryUrlByDirector(mSubcategory.getIndex_log_id()), new Response.Listener<String>() {

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

	private String getUpdateSubCategoryUrlByDirector(int indexLogId) {
		String url = Constant.BASE_URL + String.format("/api/v1/index/%d/index_log", indexLogId);
		Logger.debug(TAG, "getUpdateSubCategoryUrl -->  " + url);
		return url;
	}
	
	private String getUpdateSubCategoryUrlBySuperintendent(int indexLogId) {
		String url = Constant.BASE_URL + String.format("/api/v1/index/%d/index_log_qc", indexLogId);
		Logger.debug(TAG, "getUpdateSubCategoryUrl -->  " + url);
		return url;
	}

	// private void uploadImage(final int id) {
	// if (mNeedUploadPicPaths.size() > id) {
	// String path = mNeedUploadPicPaths.get(id);
	// Logger.debug(TAG, "uploadImage --> picPath: " + path);
	// getProgressDialogUtils().show();
	// QiniuUploadUitls.getInstance().uploadImage(getBitmap(path), new
	// IQiniuUploadUitlsListener() {
	//
	// @Override
	// public void onSucess(String fileUrl) {
	// Logger.debug(TAG, fileUrl);
	// getProgressDialogUtils().cancel();
	// List<PicUrl> picUrls = mSubcategory.getIndex_pic();
	// if (picUrls == null) {
	// picUrls = new ArrayList<>();
	// }
	// PicUrl picUrl = new PicUrl();
	// picUrl.setPic_url(fileUrl);
	// picUrls.add(picUrl);
	// mSubcategory.setIndex_pic(picUrls);
	// uploadImage(id + 1);
	// }
	//
	// @Override
	// public void onProgress(int progress) {
	//
	// }
	//
	// @Override
	// public void onError(int errorCode, String msg) {
	// getProgressDialogUtils().cancel();
	// getToast().show(getString(R.string.netword_fail) + msg);
	// }
	// });
	// } else {
	// save();
	// }
	// }

	private Bitmap getBitmap(String picPath) {
		return ImageTool.compressBitmap(picPath, 320, 240);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE_IMAGE_CAPTURE) {
			if (resultCode == RESULT_OK) {
				if (!TextUtil.isEmpty(photoUriPath)) {
					String cpmpressBitMapPath = Constant.BASE_IMAGE_CAPTURE_PATH + System.currentTimeMillis()
							+ Constant.PIC_END_STR;
					if (ImageTool.saveBitmapToJpegFile(getBitmap(photoUriPath), cpmpressBitMapPath)) {
						mPicPaths.add(cpmpressBitMapPath);
					} else {
						mPicPaths.add(photoUriPath);
					}
					mImageAdapter.setPicPaths(mPicPaths);
				} else {
					getToast().show(R.string.capture_image_fail);
				}
			}
		}
	}

}
