//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2021.11.16 at 04:48:23 PM EST 
//


package ietf.params.xml.ns.iodef_1;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:ietf:params:xml:ns:iodef-1.0}RecordData" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;attribute name="restriction" type="{urn:ietf:params:xml:ns:iodef-1.0}restriction-type" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "recordData"
})
@XmlRootElement(name = "Record")
public class Record
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "RecordData", required = true)
    protected List<RecordData> recordData;
    @XmlAttribute(name = "restriction")
    protected RestrictionType restriction;

    /**
     * Gets the value of the recordData property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the recordData property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRecordData().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RecordData }
     * 
     * 
     */
    public List<RecordData> getRecordData() {
        if (recordData == null) {
            recordData = new ArrayList<RecordData>();
        }
        return this.recordData;
    }

    /**
     * Gets the value of the restriction property.
     * 
     * @return
     *     possible object is
     *     {@link RestrictionType }
     *     
     */
    public RestrictionType getRestriction() {
        return restriction;
    }

    /**
     * Sets the value of the restriction property.
     * 
     * @param value
     *     allowed object is
     *     {@link RestrictionType }
     *     
     */
    public void setRestriction(RestrictionType value) {
        this.restriction = value;
    }

}
