package com.mapsoft.aftersale.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mapsoft.aftersale.R;

/**
 * 自定义对话框 界面更友好
 */
public class FavorDialog extends Dialog {

	public FavorDialog(Context context, int theme) {
		super(context, theme);
	}

	public FavorDialog(Context context) {
		super(context);
	}

	/**
	 * Helper class for creating a custom dialog
	 */
	public static class Builder {

		private Context context;
		private String title;
		private ArrayList<String> favors=new ArrayList<String>();
		private View contentView;
		private String positiveButtonText;
		private String negativeButtonText;
		private AdapterView.OnItemClickListener listItemClickListener;
		private String filename;
		private DialogInterface.OnClickListener
				positiveButtonClickListener,
				negativeButtonClickListener;
		public Builder(Context context) {
			this.context = context;
		}

		/**
		 * Set the Dialog message from String
		 * @param title
		 * @return
		 */
		public Builder setFavorStrings(ArrayList<String> favors) {
			this.favors=favors;
			return this;
		}

		public Builder setOnItemClickListener(AdapterView.OnItemClickListener listener) {
			this.listItemClickListener = listener;
			return this;
		}

		public String getFileName() {
			return filename;
		}

		/**
		 * Set the Dialog title from resource
		 * @param title
		 * @return
		 */
		public Builder setTitle(int title) {
			this.title = (String) context.getText(title);
			return this;
		}

		/**
		 * Set the Dialog title from String
		 * @param title
		 * @return
		 */
		public Builder setTitle(String title) {
			this.title = title;
			return this;
		}

		/**
		 * Set a custom content view for the Dialog.
		 * If a message is set, the contentView is not
		 * added to the Dialog...
		 * @param v
		 * @return
		 */
		public Builder setContentView(View v) {
			this.contentView = v;
			return this;
		}
		/**
		 * Set the positive button resource and it's listener
		 * @param positiveButtonText
		 * @param listener
		 * @return
		 */
		public Builder setPositiveButton(int positiveButtonText,
										 DialogInterface.OnClickListener listener) {
			this.positiveButtonText = (String) context
					.getText(positiveButtonText);
			this.positiveButtonClickListener = listener;
			return this;
		}

		/**
		 * Set the positive button text and it's listener
		 * @param positiveButtonText
		 * @param listener
		 * @return
		 */
		public Builder setPositiveButton(String positiveButtonText,
										 DialogInterface.OnClickListener listener) {
			this.positiveButtonText = positiveButtonText;
			this.positiveButtonClickListener = listener;
			return this;
		}

		/**
		 * Set the negative button resource and it's listener
		 * @param negativeButtonText
		 * @param listener
		 * @return
		 */
		public Builder setNegativeButton(int negativeButtonText,
										 DialogInterface.OnClickListener listener) {
			this.negativeButtonText = (String) context
					.getText(negativeButtonText);
			this.negativeButtonClickListener = listener;
			return this;
		}

		/**
		 * Set the negative button text and it's listener
		 * @param negativeButtonText
		 * @param listener
		 * @return
		 */
		public Builder setNegativeButton(String negativeButtonText,
										 DialogInterface.OnClickListener listener) {
			this.negativeButtonText = negativeButtonText;
			this.negativeButtonClickListener = listener;
			return this;
		}
		ListviewAdapter listviewAdapter;
		/**
		 * Create the custom dialog
		 */
		public FavorDialog create() {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			// instantiate the dialog with the custom Theme
			final FavorDialog dialog = new FavorDialog(context,
					R.style.Dialog);
			View layout = inflater.inflate(R.layout.favordialog, null);
			dialog.addContentView(layout, new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			// set the dialog title
			((TextView) layout.findViewById(R.id.title)).setText(title);
			listviewAdapter=new ListviewAdapter(context, favors);
			((ListView)layout.findViewById(R.id.favorlist)).setAdapter(listviewAdapter);
			if (listItemClickListener != null) {
				((ListView)layout.findViewById(R.id.favorlist))
						.setOnItemClickListener(new AdapterView.OnItemClickListener() {
							@Override
							public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3)
							{
								filename=favors.get(arg2);
								listItemClickListener.onItemClick(
										arg0, arg1,arg2,arg3 );
								dialog.dismiss();
							}
						});
			}
			if (positiveButtonText != null) {
				((Button) layout.findViewById(R.id.positiveButton))
						.setText(positiveButtonText);
				if (positiveButtonClickListener != null) {
					((Button) layout.findViewById(R.id.positiveButton))
							.setOnClickListener(new View.OnClickListener() {
								public void onClick(View v) {
									positiveButtonClickListener.onClick(
											dialog,
											DialogInterface.BUTTON_POSITIVE);
								}
							});
				}
			} else {
				// if no confirm button just set the visibility to GONE
				layout.findViewById(R.id.positiveButton).setVisibility(
						View.GONE);
			}
			// set the cancel button
			if (negativeButtonText != null) {
				((Button) layout.findViewById(R.id.negativeButton))
						.setText(negativeButtonText);
				if (negativeButtonClickListener != null) {
					((Button) layout.findViewById(R.id.negativeButton))
							.setOnClickListener(new View.OnClickListener() {
								public void onClick(View v) {
									negativeButtonClickListener.onClick(
											dialog,
											DialogInterface.BUTTON_NEGATIVE);
								}
							});
				}
			} else {
				// if no confirm button just set the visibility to GONE
				layout.findViewById(R.id.negativeButton).setVisibility(
						View.GONE);
			}
			dialog.setContentView(layout);
			return dialog;
		}
		public class ListviewAdapter extends BaseAdapter
		{
			public List<String> list;
			Context context;

			public ListviewAdapter(Context context, List<String> list) {
				super();
				this.context = context;
				this.list = list;
			}

			@Override
			public int getCount() {
				return list.size();
			}


			@Override
			public String getItem(int position) {
				return list.get(position);
			}


			@Override
			public long getItemId(int position) {
				return 0;
			}

			private class ViewHolder {
				TextView textView;
				LinearLayout layout;
			}



			@Override
			public View getView(final int position, View convertView, ViewGroup parent) {

				ViewHolder holder;
				LayoutInflater inflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

				if (convertView == null)
				{
					convertView = inflater.inflate(R.layout.list_item_favor, null);
					holder = new ViewHolder();
					holder.textView=(TextView)convertView.findViewById(R.id.line);
					holder.layout=(LinearLayout)convertView.findViewById(R.id.ly);
					holder.layout.setOnClickListener(new Button.OnClickListener()
					{
						@Override
						public void onClick(final View v) {
							final CustomDialog.Builder builder=new CustomDialog.Builder(context);
							builder.setTitle("提示").setMessage("确认删除?删除后不可恢复")
									.setPositiveButton("确认", new DialogInterface.OnClickListener() {

										@Override
										public void onClick(DialogInterface dialog, int which) {
											try{
												File file=new File(Environment.getExternalStorageDirectory() + "/"+context.getResources().getString(R.string.dirsource)+"/"+getItem(position).split(",")[0]+".txt");
												file.delete();
												favors.remove(v.getTag());
												listviewAdapter.notifyDataSetChanged();
												Toast.makeText(context, "删除成功", 1).show();
												dialog.dismiss();
											}
											catch (Exception e) {
											}
										}


									})
									.setNegativeButton("取消",  new DialogInterface.OnClickListener() {

										@Override
										public void onClick(DialogInterface dialog, int which) {
											dialog.dismiss();
										}
									}).create().show();

						}
					});
					convertView.setTag(holder);
				}
				else
				{
					holder = (ViewHolder) convertView.getTag();
				}

				holder.textView.setText(getItem(position).split(",")[0]);
				holder.layout.setTag(getItem(position));
				return convertView;
			}
		}
	}


}