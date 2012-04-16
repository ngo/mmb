package ru.mmb.terminal.activity.input.team;

import java.util.List;

import ru.mmb.terminal.R;
import ru.mmb.terminal.activity.input.team.model.TeamListRecord;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

public class TeamsAdapter extends ArrayAdapter<TeamListRecord>
{
	private TeamFilter filter = null;

	public TeamsAdapter(Context context, int textViewResourceId, List<TeamListRecord> items)
	{
		super(context, textViewResourceId, items);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View view = convertView;
		if (view == null)
		{
			LayoutInflater inflater =
			    (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.input_team_row, null);
		}
		TeamListRecord item = getItem(position);
		if (item != null)
		{
			TextView tvNum = (TextView) view.findViewById(R.id.inputTeamRow_numText);
			TextView tvTeam = (TextView) view.findViewById(R.id.inputTeamRow_teamText);
			TextView tvMember = (TextView) view.findViewById(R.id.inputTeamRow_memberText);
			if (tvNum != null) tvNum.setText(Integer.toString(item.getTeamNumber()));
			if (tvTeam != null) tvTeam.setText(item.getTeamName());
			if (tvMember != null) tvMember.setText(item.getMemberText());
		}
		return view;
	}

	@Override
	public Filter getFilter()
	{
		if (filter == null)
		{
			filter = new TeamFilter(this);
		}
		return filter;
	}

	@Override
	public long getItemId(int position)
	{
		return getItem(position).getTeamId();
	}
}