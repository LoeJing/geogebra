/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/


package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoPointND;


/**
 * Compute a line through a point and orthogonal to ...
 *
 * @author  matthieu
 * @version 
 */
public abstract class AlgoOrtho extends AlgoElement3D {

 
	private GeoPointND point; // input
    private GeoElement inputOrtho; // input
    private GeoLine3D line; // output       


    public AlgoOrtho(Construction cons, String label, GeoPointND point, GeoElement ortho) {
        super(cons);
        this.point = point;
        this.inputOrtho = ortho;
        line = new GeoLine3D(cons);
        
        setInputOutput(new GeoElement[] {(GeoElement) point, ortho}, new GeoElement[] {line});

        // compute line 
        compute();
        line.setLabel(label);
    }


    public GeoLine3D getLine() {
        return line;
    }
    
    protected GeoPointND getPoint(){
    	return point;
    }

    protected GeoElement getInputOrtho(){
    	return inputOrtho;
    }


    @Override
	final public String toString(StringTemplate tpl) {
    	return app.getPlain("LineThroughAPerpendicularToB",point.getLabel(tpl),inputOrtho.getLabel(tpl));
    }
}
