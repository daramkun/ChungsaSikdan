package me.daram.chungsasikdan;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class LoadingDialog extends Dialog {
	public LoadingDialog (Context context) {
		super ( context );
		
		requestWindowFeature ( Window.FEATURE_NO_TITLE );
		
		getWindow ().setBackgroundDrawable ( new ColorDrawable ( android.graphics.Color.argb ( 128, 32, 32, 32 ) ) );
		setCanceledOnTouchOutside ( false );
	}
	
	@Override
	public void onCreate ( Bundle savedInstanceState ) {
		super.onCreate ( savedInstanceState );
		
		setContentView ( R.layout.loading_dialog );
		
		ImageView imageView = ( ImageView ) findViewById ( R.id.loading_dialog_image );
		Animation anime = AnimationUtils.loadAnimation ( getContext (), R.anim.loading );
		imageView.setAnimation ( anime );
	}
}
