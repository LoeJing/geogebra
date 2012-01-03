package geogebra3D.kernel3D.commands;

import geogebra.common.kernel.AbstractKernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CmdVector;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.MyError;
import geogebra3D.kernel3D.GeoPoint3D;
import geogebra3D.kernel3D.Kernel3D;



/*
 * Vector[ <GeoPoint3D>, <GeoPoint3D> ] or CmdVector
 */
public class CmdVector3D extends CmdVector {
	

	public CmdVector3D(AbstractKernel kernel) {
		super(kernel);
		
		
	}
	
	
	
	public GeoElement[] process(Command c) throws MyError {	


		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;



		switch (n) {
		case 2 :
			arg = resArgs(c);
			//if one of the args is a 3D point, 
			//then the vector is a 3D vector
			//else it will be a 2D (calling super method)
			if (arg[0].isGeoElement3D() || arg[1].isGeoElement3D()){
				if ((ok[0] = (arg[0] .isGeoPoint()))
						&& (ok[1] = (arg[1] .isGeoPoint()))) {
					GeoElement[] ret =
					{
							((AbstractKernel)kernelA).getManager3D().Vector3D(
									c.getLabel(),
									(GeoPointND) arg[0],
									(GeoPointND) arg[1])};
					return ret;
				} else {
					if (!ok[0])
						throw argErr(app, c.getName(), arg[0]);
					else
						throw argErr(app, c.getName(), arg[1]);
				}
			}
		default :

		}


		return super.process(c);
	}

}
