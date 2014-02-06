package pt.lsts.accu.panel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import pt.up.fe.dceg.accu.R;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AccuAction 
{
	String name() default "PANEL paaaneeel";
//	String author() default "LSTS-FEUP";
//	String description() default "";
//	String version() default "1.0";
//	String documentation() default "";
//	String icon() default "";
//	String category() default "";

	int icon() default R.drawable.icon;
}
