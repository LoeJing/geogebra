package geogebra3D.kernel3D.commands;

import geogebra.common.kernel.AbstractKernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoCoordSys2D;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.MyError;
import geogebra3D.kernel3D.GeoQuadric3DLimited;

public class CmdBottom extends CommandProcessor {
		
	public CmdBottom(AbstractKernel kernel) {
		super(kernel);
	}

	
	

	public GeoElement[] process(Command c) throws MyError {
	    int n = c.getArgumentNumber();
	    boolean[] ok = new boolean[n];
	    GeoElement[] arg;

	    switch (n) {
	    case 1 :
	    	arg = resArgs(c);
	    	if (
	    			(ok[0] = (arg[0] instanceof GeoQuadric3DLimited ) )
	    	) {
	    		GeoElement[] ret =
	    		{
	    				kernelA.getManager3D().QuadricBottom(
	    						c.getLabel(),
	    						(GeoQuadric3DLimited) arg[0])};
	    		return ret;
	    	}else{
	    		throw argErr(app, c.getName(), arg[0]);
	    	}
	    	

	    default :
	    	throw argNumErr(app, c.getName(), n);
	    }
	    

	}
	
}
