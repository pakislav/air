package hr.foi.air.kriptocavrljanje.adapters;

import java.util.ArrayList;
import java.util.List;

import hr.foi.air.crypto_chat.R;
import hr.foi.air.kriptocavrljanje.core.Comment;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ListAdapter extends ArrayAdapter<Comment> {

	private TextView comment;
	private LinearLayout wrapper;
	private List<Comment> list = new ArrayList<Comment>();
	
	@Override
	public void add(Comment comment) {
		list.add(comment);
		super.add(comment);
	}
	
	public ListAdapter(Context context, int resource) {
		super(context, resource);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View row = convertView;
		
		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.listrow_layout, parent, false);
		}

		wrapper = (LinearLayout) row.findViewById(R.id.wrapper);

		Comment c = getItem(position);

		comment = (TextView) row.findViewById(R.id.comment);

		comment.setText(c.getComment());

		comment.setBackgroundResource(c.isSide() ? R.drawable.bubble_yellow : R.drawable.bubble_green);
		wrapper.setGravity(c.isSide() ? Gravity.LEFT : Gravity.RIGHT);   // Stavljanje komentara na stranu ovisno kome pripada

		return row;
	}
	
}
