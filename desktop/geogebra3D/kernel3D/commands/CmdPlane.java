package geogebra3D.kernel3D.commands;

import geogebra.common.kernel.AbstractKernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoCoordSys2D;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.MyError;


public class CmdPlane extends CommandProcessor {
	
	
	
	
	public CmdPlane(AbstractKernel kernel) {
		super(kernel);
	}

	
	

	public GeoElement[] process(Command c) throws MyError {
	    int n = c.getArgumentNumber();
	    boolean[] ok = new boolean[n];
	    GeoElement[] arg;

	    switch (n) {	    
	    case 1 :
	    	arg = resArgs(c);
	    	if (arg[0] instanceof GeoCoordSys2D )
	    	{GeoElement[] ret =
	    	{
	    			(GeoElement) kernelA.getManager3D().Plane3D(
	    					c.getLabel(),
	    					(GeoCoordSys2D) arg[0])};
	    	return ret;
	    	}else{
	    		throw argErr(app, c.getName(), arg[0]);
	    	}
	    case 2 :
	    	arg = resArgs(c);
	    	if (
	    			(ok[0] = (arg[0] .isGeoPoint() ) )
	    			&& (ok[1] = (arg[1] instanceof GeoLineND ))
	    	) {
	    		GeoElement[] ret =
	    		{
	    				(GeoElement) kernelA.getManager3D().Plane3D(
	    						c.getLabel(),
	    						(GeoPointND) arg[0],
	    						(GeoLineND) arg[1])};
	    		return ret;
	    	}else if (
	    			(ok[0] = (arg[0] .isGeoPoint() ) )
	    			&& (ok[1] = (arg[1] instanceof GeoCoordSys2D ))
	    	) {
	    		GeoElement[] ret =
	    		{
	    				(GeoElement) kernelA.getManager3D().Plane3D(
	    						c.getLabel(),
	    						(GeoPointND) arg[0],
	    						(GeoCoordSys2D) arg[1])};
	    		return ret;

	    	}else{
	    		if (!ok[0])
	    			throw argErr(app, c.getName(), arg[0]);
	    		else 
	    			throw argErr(app, c.getName(), arg[1]);
	    	}

	    case 3 :
	    	arg = resArgs(c);
	    	if ((ok[0] = (arg[0] .isGeoPoint() ) )
	    			&& (ok[1] = (arg[1] .isGeoPoint()  ))
	    			&& (ok[2] = (arg[2] .isGeoPoint()  ))) {
	    		GeoElement[] ret =
	    		{
	    				kernelA.getManager3D().Plane3D(
	    						c.getLabel(),
	    						(GeoPointND) arg[0],
	    						(GeoPointND) arg[1],
	    						(GeoPointND) arg[2])};
	    		return ret;
	    	}else{
	    		if (!ok[0])
	    			throw argErr(app, c.getName(), arg[0]);
	    		else if (!ok[1])
	    			throw argErr(app, c.getName(), arg[1]);
	    		else
	    			throw argErr(app, c.getName(), arg[2]);
	    	}

	    default :
	    	throw argNumErr(app, c.getName(), n);
	    }
	    

	}
	
}
