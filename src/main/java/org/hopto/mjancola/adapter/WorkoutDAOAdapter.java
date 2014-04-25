package org.hopto.mjancola.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import org.hopto.mjancola.R;
import org.hopto.mjancola.model.LearningMode;
import org.hopto.mjancola.utility.Converter;
import org.hopto.mjancola.utility.WorkoutDAO;

import java.util.Calendar;

public class WorkoutDAOAdapter extends ArrayAdapter<WorkoutDAO>
{

    Context context;
    int     layoutResourceId;
    WorkoutDAO data[] = null;

    public WorkoutDAOAdapter( Context context, int layoutResourceId, WorkoutDAO[] data )
    {
        super( context, layoutResourceId, data );
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView( int position, View convertView, ViewGroup parent )
    {
        View row = convertView;
        WorkoutDAOHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new WorkoutDAOHolder();
            holder.imgIcon = (ImageView)row.findViewById(R.id.workout_icon);
            holder.type = (TextView)row.findViewById( R.id.workout_type);
            holder.date = (TextView)row.findViewById( R.id.workout_date );
            holder.duration = (TextView)row.findViewById( R.id.workout_duration );
            holder.distance = (TextView) row.findViewById( R.id.workout_distance);
            holder.startReason = (TextView) row.findViewById( R.id.workout_start_reason );
            holder.endReason = (TextView) row.findViewById( R.id.workout_end_reason );

            row.setTag(holder);
        }
        else
        {
            holder = (WorkoutDAOHolder)row.getTag();
        }

        WorkoutDAO workoutDAO = data[position];
        if (workoutDAO.getType().equals( LearningMode.WALK_NAME))
        {
            holder.imgIcon.setImageResource( R.drawable.hike_white_100 );
            holder.type.setText("");
        }
        else if (workoutDAO.getType().equals( LearningMode.RUN_NAME))
        {
            holder.imgIcon.setImageResource( R.drawable.run_white_100 );
            holder.type.setText("");
        }
        else if (workoutDAO.getType().equals( LearningMode.BIKE_NAME))
        {
            holder.imgIcon.setImageResource( R.drawable.bike_white_100 );
            holder.type.setText("");
        }
        else
        {
            holder.imgIcon.setImageResource( R.drawable.none_20);
            holder.type.setText(workoutDAO.getType());
        }
        //holder.imgIcon.setImageResource(some image);
        Calendar due = Calendar.getInstance();
        due.setTimeInMillis( workoutDAO.getStart() );
        holder.date.setText( due.getTime().toString() );
        String duration = Converter.formatDuration( workoutDAO.getDuration() );
        if (duration != null)
        {
            holder.duration.setText(duration);
        }

        holder.distance.setText( Converter.formatDistance( (workoutDAO.getDistance())/100 ) + " mi");
        holder.startReason.setText( workoutDAO.getStartReason() );
        holder.endReason.setText( workoutDAO.getEndReason() );


        return row;
    }

    static class WorkoutDAOHolder
    {
        ImageView imgIcon;
        TextView  type;
        TextView  date;
        TextView  distance;
        TextView  duration;
        TextView  startReason;
        TextView  endReason;
    }
}
