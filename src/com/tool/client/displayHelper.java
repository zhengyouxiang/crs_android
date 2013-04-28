package com.tool.client;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.client.MyApplication;
import com.main.DataActivity;

import android.content.Context;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class displayHelper {
	
	JSONObject display;
	JSONArray output_column_display = new JSONArray();
	JSONArray input_column_display = new JSONArray();
	
	JSONObject output_column_list;
	JSONObject input_column_list;
	
	Context context;
	
	private Map<String,Integer> columnIdMap = new HashMap<String,Integer>();
	
	public static final int DEFAULT = 0;
	public static final int INLINE = 1;
	public static final int CONTENT = 2;
	
	public static final int INPUT = 0;
	public static final int OUTPUT = 1;
	
	private static Map<String,Integer> columnMap = new HashMap<String,Integer>();
	
	public displayHelper(Context c) {
		context = c;
		columnMap.put("_CONTENT", CONTENT);
		columnMap.put("_INLINE", INLINE);
	}
	
	public void setDisplay(String str) throws JSONException {
		display = new JSONObject(str);
		input_column_display = display.getJSONArray("input_column");
		output_column_display = display.getJSONArray("output_column");
	}
	
	public void genOutputDisplay() throws JSONException {
		if(output_column_display.length() != 0)
			return;
		Iterator it = output_column_list.keys();
		while(it.hasNext()) {
			String column_name_en = (String) it.next();
			JSONObject jo = new JSONObject();
			jo.put("column", column_name_en);
			output_column_display.put(jo);
		}
		System.out.println(output_column_display.toString());
	}
	
	public void genInputDisplay() throws JSONException {
		if(input_column_display.length() != 0)
			return;
		Iterator it = input_column_list.keys();
		while(it.hasNext()) {
			String column_name_en = (String) it.next();
			JSONObject jo = new JSONObject();
			jo.put("column", column_name_en);
			input_column_display.put(jo);
		}
		System.out.println(input_column_display.toString());
	}
	
	public void setOutputColumnList(JSONObject jo) throws JSONException {
		output_column_list = jo;
		// If output display doesn't exists, generate it
		genOutputDisplay();
	}
	
	public void setInputColumnList(JSONObject jo) throws JSONException {
		input_column_list = jo;
		// If input display doesn't exists, generate it
		genInputDisplay();
	}
	
	public int getColumnType(int pos, int type) {
		int n = DEFAULT;
		String column = "";
		try {
			switch (type) {
			case INPUT:
				column = input_column_display.getJSONObject(pos).getString("column");
				break;
			case OUTPUT:
				column = output_column_display.getJSONObject(pos).getString("column");
				break;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if(columnMap.containsKey(column))
			n = columnMap.get(column);
		return n;
	}

	public LinearLayout getOutputLayout(int pos) {
		
		LinearLayout layout = new LinearLayout(context);
		layout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,1.0f));
		
		int type = getColumnType(pos, OUTPUT);
		
		String column_name_en = "";
		String column_name = "";
		String content = "";
		
		switch(type){
		case DEFAULT:
			
			try {
				column_name_en = output_column_display.getJSONObject(pos).getString("column");
				column_name = output_column_list.getJSONObject(column_name_en).getString("column_name");
				content = output_column_list.getJSONObject(column_name_en).getString("content");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			
			TextView v11 = new TextView(context);
			v11.setText(column_name);
			v11.setTextSize(MyApplication.TEXT_SIZE_MIDIUM);
			v11.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			v11.setWidth(150);
			
			TextView v12 = new TextView(context);
			v12.setText(content);
			v12.setTextSize(MyApplication.TEXT_SIZE_SMALL);
			v12.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,1.0f));
			
			layout.addView(v11);
			layout.addView(v12);
			
			break;
			
		case INLINE:
			break;
		case CONTENT:
			try {
				content = output_column_display.getJSONObject(pos).getString("content");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			TextView v21 = new TextView(context);
			v21.setText(content);
			v21.setTextSize(MyApplication.TEXT_SIZE_SMALL);
			v21.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,1.0f)); 
			
			layout.addView(v21);
			
			break;
		}
		
		return layout;
	}
	
	private LinearLayout getInputLayoutContent(int pos, String column_type) {
		
		LinearLayout layout = new LinearLayout(context);
		int type = MyApplication.inputTypeMap.containsKey(column_type)?MyApplication.inputTypeMap.get(column_type):MyApplication.TEXT;
		
		String column_name_en = "";
		String column_type_option = "";
		
		try {
			column_name_en = input_column_display.getJSONObject(pos).getString("column");
			column_type_option = input_column_list.getJSONObject(column_name_en).getString("column_type_option");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		//TODO changeId
		int id = MyApplication.generateId();
		columnIdMap.put(column_name_en, id);
		
		if(column_name_en.equals("longitude") || column_name_en.equals("latitude"))
			DataActivity.buttonHandler.sendEmptyMessage(MyApplication.GET_POS);
		
		switch(type) {
		case MyApplication.BOOLEAN:
		case MyApplication.MULTI_CHOICE:
			RadioGroup rg = new RadioGroup(context);
			rg.setId(id);
			String[] choices = column_type_option.split(",");
			for(int i=0;i<choices.length;i++) {
				RadioButton rb = new RadioButton(context);
				rb.setText(choices[i]);
				//TODO changeId
				rb.setId(id+i+1);
				rg.addView(rb);
			}
			layout.addView(rg);
			break;
		case MyApplication.TEXT:
		case MyApplication.MULTI_TEXT:
			EditText et = new EditText(context);
			et.setId(id);
			et.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,1.0f));
			layout.addView(et);
			break;
		case MyApplication.INTEGER:
		case MyApplication.FLOAT:
			EditText etf = new EditText(context);
			etf.setRawInputType(InputType.TYPE_CLASS_NUMBER);
			etf.setId(id);
			etf.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,1.0f));
			layout.addView(etf);
			break;
		case MyApplication.PICTURE:
			TextView textView = new TextView(context);
			textView.setId(id);
			textView.setLines(5);
			textView.setVisibility(View.GONE);
			textView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,1.0f));
			
			Button getPhotoBtn = new Button(context);
			getPhotoBtn.setText("选择图片...");
			getPhotoBtn.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,1.0f));
			
//			DataActivity.addButton(getPhotoBtn);
//			DataActivity.addButtonId(id);
//			DataActivity.buttonHandler.sendEmptyMessage(MyApplication.TAKE_PHOTO);
			
			LinearLayout innerLayout = new LinearLayout(context);
			innerLayout.setOrientation(LinearLayout.VERTICAL);
			
			innerLayout.addView(getPhotoBtn);
			innerLayout.addView(textView);
			
			layout.addView(innerLayout);

			break;
		}
		
		return layout;
	}
	
	public LinearLayout getInputLayout(int pos) {
		
		LinearLayout layout = new LinearLayout(context);
		int type = getColumnType(pos, INPUT);
		
		String column_name_en = "";
		String column_name = "";
		String column_type = "";
		
		switch(type){
		case DEFAULT:
			
			try {
				column_name_en = input_column_display.getJSONObject(pos).getString("column");
				column_name = input_column_list.getJSONObject(column_name_en).getString("column_name");
				column_type = input_column_list.getJSONObject(column_name_en).getString("column_type");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			TextView v11 = new TextView(context);
			v11.setText(column_name);
			v11.setTextSize(MyApplication.TEXT_SIZE_MIDIUM);
			v11.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			v11.setWidth(150);
			
			LinearLayout v12 = getInputLayoutContent(pos, column_type);
			v12.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,1.0f));
			
			layout.addView(v11);
			layout.addView(v12);
			
			break;
			
		case INLINE:
			break;
		}
		
		return layout;
	}
	
	public JSONArray getForm() {
		JSONArray ja = new JSONArray();
		Iterator it = input_column_list.keys();
		while(it.hasNext()) {
			String key = (String) it.next();
			int value = columnIdMap.containsKey(key)?columnIdMap.get(key):0;
			try {
				JSONObject jo = input_column_list.getJSONObject(key);
				String column_name = jo.getString("column_name");
				String type = jo.getString("column_type");
				int not_null = jo.getInt("not_null");
				JSONObject jor = new JSONObject();
				jor.put("column_name", column_name);
				jor.put("column_name_en", key);
				jor.put("column_type", type);
				jor.put("column_id", value);
				jor.put("not_null", not_null);
				ja.put(jor);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return ja;
	}

	public Map<String, Integer> getColumnIdMap() {
		return columnIdMap;
	}
	
	public int getOutput_column_num() {
		return output_column_display.length();
	}
	
	public int getInput_column_num() {
		return input_column_display.length();
	}
	
	public Integer getColumnId(String column_name_en) {
		if(columnIdMap.containsKey(column_name_en))
			return columnIdMap.get(column_name_en);
		return 0;
	}
	
}
