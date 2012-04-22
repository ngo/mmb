package ru.mmb.terminal.activity.input.withdraw;

import static ru.mmb.terminal.activity.Constants.KEY_CURRENT_INPUT_WITHDRAWN_CHECKED;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.mmb.terminal.R;
import ru.mmb.terminal.activity.input.InputActivityState;
import ru.mmb.terminal.activity.input.withdraw.model.TeamMemberRecord;
import ru.mmb.terminal.db.TerminalDB;
import ru.mmb.terminal.model.Participant;
import ru.mmb.terminal.model.Team;
import ru.mmb.terminal.model.registry.TeamsRegistry;
import android.app.Activity;
import android.os.Bundle;

public class WithdrawMemberActivityState extends InputActivityState
{
	private final List<Participant> prevWithdrawnMembers = new ArrayList<Participant>();
	private final List<Participant> currWithdrawnMembers = new ArrayList<Participant>();

	public WithdrawMemberActivityState()
	{
		super("input.withdraw");
	}

	public List<Participant> getAllWithdrawnMembers()
	{
		List<Participant> result = new ArrayList<Participant>();
		result.addAll(prevWithdrawnMembers);
		result.addAll(currWithdrawnMembers);
		return result;
	}

	public boolean isPrevWithdrawn(Participant member)
	{
		return prevWithdrawnMembers.contains(member);
	}

	public boolean isCurrWithdrawn(Participant member)
	{
		return currWithdrawnMembers.contains(member);
	}

	public void setCurrWithdrawn(Participant member, boolean withdraw)
	{
		if (withdraw)
		{
			if (!currWithdrawnMembers.contains(member))
			{
				currWithdrawnMembers.add(member);
				Collections.sort(currWithdrawnMembers);
				fireStateChanged();
			}
		}
		else
		{
			if (currWithdrawnMembers.contains(member))
			{
				currWithdrawnMembers.remove(member);
				fireStateChanged();
			}
		}
	}

	@Override
	protected void update()
	{
		super.update();
		updatePrevWithdrawnMembers();
	}

	private void updatePrevWithdrawnMembers()
	{
		if (getCurrentLevel() != null && getCurrentTeam() != null)
		{
			prevWithdrawnMembers.clear();
			// TODO restore previously withdrawn request
			// prevWithdrawnMembers.addAll(TerminalDB.getInstance().getWithdrawnMembers(getCurrentLevel(), getCurrentTeam()));
		}
	}

	public CharSequence getResultText(Activity context)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(context.getResources().getString(R.string.input_withdraw_res_members));
		sb.append("\n");
		if (currWithdrawnMembers.size() == 0)
		{
			sb.append(context.getResources().getString(R.string.input_withdraw_res_no_members));
		}
		else
		{
			for (int i = 0; i < currWithdrawnMembers.size(); i++)
			{
				if (i > 0) sb.append("; ");
				Participant member = currWithdrawnMembers.get(i);
				sb.append(member.getUserName());
			}
		}
		return sb.toString();
	}

	public List<TeamMemberRecord> getMemberRecords()
	{
		List<TeamMemberRecord> result = new ArrayList<TeamMemberRecord>();
		for (Participant member : getCurrentTeam().getMembers())
		{
			result.add(new TeamMemberRecord(member, isPrevWithdrawn(member)));
		}
		Collections.sort(result);
		return result;
	}

	@Override
	public void save(Bundle savedInstanceState)
	{
		super.save(savedInstanceState);
		savedInstanceState.putSerializable(KEY_CURRENT_INPUT_WITHDRAWN_CHECKED, (Serializable) getCurrWithdrawnIds());
	}

	private List<Integer> getCurrWithdrawnIds()
	{
		List<Integer> result = new ArrayList<Integer>();
		for (Participant member : currWithdrawnMembers)
		{
			result.add(member.getUserId());
		}
		return result;
	}

	@Override
	public void load(Bundle savedInstanceState)
	{
		super.load(savedInstanceState);
		currWithdrawnMembers.clear();
		if (savedInstanceState.containsKey(KEY_CURRENT_INPUT_WITHDRAWN_CHECKED))
		{
			@SuppressWarnings("unchecked")
			List<Integer> idList =
			    (List<Integer>) savedInstanceState.getSerializable(KEY_CURRENT_INPUT_WITHDRAWN_CHECKED);
			loadCurrWithdrawnMembers(idList);
		}
	}

	private void loadCurrWithdrawnMembers(List<Integer> idList)
	{
		if (getCurrentTeam() != null)
		{
			int teamId = getCurrentTeam().getTeamId();
			Team team = TeamsRegistry.getInstance().getTeamById(teamId);
			if (team != null)
			{
				for (Integer participantId : idList)
				{
					Participant member = team.getMember(participantId);
					if (member != null && !currWithdrawnMembers.contains(member))
					    currWithdrawnMembers.add(member);
				}
			}
		}
	}

	public void saveCurrWithdrawnToDB()
	{
		TerminalDB.getInstance().saveWithdrawnMembers(getCurrentLevel(), getCurrentTeam(), currWithdrawnMembers);
	}
}
