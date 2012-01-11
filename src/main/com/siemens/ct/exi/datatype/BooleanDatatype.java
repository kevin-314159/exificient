/*
 * Copyright (C) 2007-2011 Siemens AG
 *
 * This program and its interfaces are free software;
 * you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.siemens.ct.exi.datatype;

import java.io.IOException;

import javax.xml.namespace.QName;

import com.siemens.ct.exi.context.DecoderContext;
import com.siemens.ct.exi.context.EncoderContext;
import com.siemens.ct.exi.context.QNameContext;
import com.siemens.ct.exi.datatype.charset.XSDBooleanCharacterSet;
import com.siemens.ct.exi.io.channel.DecoderChannel;
import com.siemens.ct.exi.io.channel.EncoderChannel;
import com.siemens.ct.exi.types.BuiltInType;
import com.siemens.ct.exi.values.BooleanValue;
import com.siemens.ct.exi.values.Value;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.8
 */

public class BooleanDatatype extends AbstractDatatype {

	private static final long serialVersionUID = -6150310956233103627L;

	protected BooleanValue bool;

	public BooleanDatatype(QName schemaType) {
		super(BuiltInType.BOOLEAN, schemaType);
		this.rcs = new XSDBooleanCharacterSet();
	}

	public boolean isValidString(String value) {
		bool = BooleanValue.parse(value);
		return (bool != null);
	}

	public boolean isValid(Value value) {
		if (value instanceof BooleanValue) {
			bool = (BooleanValue) value;
			return true;
		} else {
			return isValidString(value.toString());
		}
	}

	@Override
	public boolean isValidRCS(Value value) {
		// Note: boolean really needs to do a check since it can be used for
		// xsi:nil
		super.isValidRCS(value);
		return isValid(value);
	}

	public boolean getBoolean() {
		return bool.toBoolean();
	}

	public void writeValue(EncoderContext encoderContext,
			QNameContext qnContext, EncoderChannel valueChannel)
			throws IOException {
		valueChannel.encodeBoolean(bool.toBoolean());
	}

	public Value readValue(DecoderContext decoderContext,
			QNameContext qnContext, DecoderChannel valueChannel)
			throws IOException {
		return valueChannel.decodeBooleanValue();
	}
}