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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


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
 *         &lt;choice maxOccurs="unbounded">
 *           &lt;element name="NodeName" type="{urn:ietf:params:xml:ns:iodef-1.0}MLStringType" minOccurs="0"/>
 *           &lt;element ref="{urn:ietf:params:xml:ns:iodef-1.0}Address" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;/choice>
 *         &lt;element ref="{urn:ietf:params:xml:ns:iodef-1.0}Location" minOccurs="0"/>
 *         &lt;element ref="{urn:ietf:params:xml:ns:iodef-1.0}DateTime" minOccurs="0"/>
 *         &lt;element ref="{urn:ietf:params:xml:ns:iodef-1.0}NodeRole" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{urn:ietf:params:xml:ns:iodef-1.0}Counter" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "nodeNameOrAddress",
    "location",
    "dateTime",
    "nodeRole",
    "counter"
})
@XmlRootElement(name = "Node")
public class Node
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElements({
        @XmlElement(name = "NodeName", type = MLStringType.class),
        @XmlElement(name = "Address", type = Address.class)
    })
    protected List<Serializable> nodeNameOrAddress;
    @XmlElement(name = "Location")
    protected MLStringType location;
    @XmlElement(name = "DateTime")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dateTime;
    @XmlElement(name = "NodeRole")
    protected List<NodeRole> nodeRole;
    @XmlElement(name = "Counter")
    protected List<Counter> counter;

    /**
     * Gets the value of the nodeNameOrAddress property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the nodeNameOrAddress property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNodeNameOrAddress().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MLStringType }
     * {@link Address }
     * 
     * 
     */
    public List<Serializable> getNodeNameOrAddress() {
        if (nodeNameOrAddress == null) {
            nodeNameOrAddress = new ArrayList<Serializable>();
        }
        return this.nodeNameOrAddress;
    }

    /**
     * Gets the value of the location property.
     * 
     * @return
     *     possible object is
     *     {@link MLStringType }
     *     
     */
    public MLStringType getLocation() {
        return location;
    }

    /**
     * Sets the value of the location property.
     * 
     * @param value
     *     allowed object is
     *     {@link MLStringType }
     *     
     */
    public void setLocation(MLStringType value) {
        this.location = value;
    }

    /**
     * Gets the value of the dateTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDateTime() {
        return dateTime;
    }

    /**
     * Sets the value of the dateTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDateTime(XMLGregorianCalendar value) {
        this.dateTime = value;
    }

    /**
     * Gets the value of the nodeRole property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the nodeRole property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNodeRole().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link NodeRole }
     * 
     * 
     */
    public List<NodeRole> getNodeRole() {
        if (nodeRole == null) {
            nodeRole = new ArrayList<NodeRole>();
        }
        return this.nodeRole;
    }

    /**
     * Gets the value of the counter property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the counter property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCounter().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Counter }
     * 
     * 
     */
    public List<Counter> getCounter() {
        if (counter == null) {
            counter = new ArrayList<Counter>();
        }
        return this.counter;
    }

}
