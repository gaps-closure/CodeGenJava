//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2021.11.16 at 04:48:23 PM EST 
//


package ietf.params.xml.ns.iodef_1;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for restriction-type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="restriction-type">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *     &lt;enumeration value="default"/>
 *     &lt;enumeration value="public"/>
 *     &lt;enumeration value="need-to-know"/>
 *     &lt;enumeration value="private"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "restriction-type")
@XmlEnum
public enum RestrictionType {

    @XmlEnumValue("default")
    DEFAULT("default"),
    @XmlEnumValue("public")
    PUBLIC("public"),
    @XmlEnumValue("need-to-know")
    NEED_TO_KNOW("need-to-know"),
    @XmlEnumValue("private")
    PRIVATE("private");
    private final String value;

    RestrictionType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static RestrictionType fromValue(String v) {
        for (RestrictionType c: RestrictionType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
