//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2021.11.16 at 04:48:23 PM EST 
//


package ietf.params.xml.ns.iodef_1;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the ietf.params.xml.ns.iodef_1 package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Location_QNAME = new QName("urn:ietf:params:xml:ns:iodef-1.0", "Location");
    private final static QName _RecordItem_QNAME = new QName("urn:ietf:params:xml:ns:iodef-1.0", "RecordItem");
    private final static QName _ContactName_QNAME = new QName("urn:ietf:params:xml:ns:iodef-1.0", "ContactName");
    private final static QName _Application_QNAME = new QName("urn:ietf:params:xml:ns:iodef-1.0", "Application");
    private final static QName _Fax_QNAME = new QName("urn:ietf:params:xml:ns:iodef-1.0", "Fax");
    private final static QName _ReportTime_QNAME = new QName("urn:ietf:params:xml:ns:iodef-1.0", "ReportTime");
    private final static QName _DateTime_QNAME = new QName("urn:ietf:params:xml:ns:iodef-1.0", "DateTime");
    private final static QName _IncidentID_QNAME = new QName("urn:ietf:params:xml:ns:iodef-1.0", "IncidentID");
    private final static QName _URL_QNAME = new QName("urn:ietf:params:xml:ns:iodef-1.0", "URL");
    private final static QName _DetectTime_QNAME = new QName("urn:ietf:params:xml:ns:iodef-1.0", "DetectTime");
    private final static QName _StartTime_QNAME = new QName("urn:ietf:params:xml:ns:iodef-1.0", "StartTime");
    private final static QName _Telephone_QNAME = new QName("urn:ietf:params:xml:ns:iodef-1.0", "Telephone");
    private final static QName _EndTime_QNAME = new QName("urn:ietf:params:xml:ns:iodef-1.0", "EndTime");
    private final static QName _Email_QNAME = new QName("urn:ietf:params:xml:ns:iodef-1.0", "Email");
    private final static QName _Description_QNAME = new QName("urn:ietf:params:xml:ns:iodef-1.0", "Description");
    private final static QName _Timezone_QNAME = new QName("urn:ietf:params:xml:ns:iodef-1.0", "Timezone");
    private final static QName _OperatingSystem_QNAME = new QName("urn:ietf:params:xml:ns:iodef-1.0", "OperatingSystem");
    private final static QName _AdditionalData_QNAME = new QName("urn:ietf:params:xml:ns:iodef-1.0", "AdditionalData");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: ietf.params.xml.ns.iodef_1
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SoftwareType }
     * 
     */
    public SoftwareType createSoftwareType() {
        return new SoftwareType();
    }

    /**
     * Create an instance of {@link ContactMeansType }
     * 
     */
    public ContactMeansType createContactMeansType() {
        return new ContactMeansType();
    }

    /**
     * Create an instance of {@link MLStringType }
     * 
     */
    public MLStringType createMLStringType() {
        return new MLStringType();
    }

    /**
     * Create an instance of {@link Address }
     * 
     */
    public Address createAddress() {
        return new Address();
    }

    /**
     * Create an instance of {@link HistoryItem }
     * 
     */
    public HistoryItem createHistoryItem() {
        return new HistoryItem();
    }

    /**
     * Create an instance of {@link IncidentIDType }
     * 
     */
    public IncidentIDType createIncidentIDType() {
        return new IncidentIDType();
    }

    /**
     * Create an instance of {@link Contact }
     * 
     */
    public Contact createContact() {
        return new Contact();
    }

    /**
     * Create an instance of {@link RegistryHandle }
     * 
     */
    public RegistryHandle createRegistryHandle() {
        return new RegistryHandle();
    }

    /**
     * Create an instance of {@link PostalAddress }
     * 
     */
    public PostalAddress createPostalAddress() {
        return new PostalAddress();
    }

    /**
     * Create an instance of {@link ExtensionType }
     * 
     */
    public ExtensionType createExtensionType() {
        return new ExtensionType();
    }

    /**
     * Create an instance of {@link Assessment }
     * 
     */
    public Assessment createAssessment() {
        return new Assessment();
    }

    /**
     * Create an instance of {@link Impact }
     * 
     */
    public Impact createImpact() {
        return new Impact();
    }

    /**
     * Create an instance of {@link TimeImpact }
     * 
     */
    public TimeImpact createTimeImpact() {
        return new TimeImpact();
    }

    /**
     * Create an instance of {@link MonetaryImpact }
     * 
     */
    public MonetaryImpact createMonetaryImpact() {
        return new MonetaryImpact();
    }

    /**
     * Create an instance of {@link Counter }
     * 
     */
    public Counter createCounter() {
        return new Counter();
    }

    /**
     * Create an instance of {@link Confidence }
     * 
     */
    public Confidence createConfidence() {
        return new Confidence();
    }

    /**
     * Create an instance of {@link Node }
     * 
     */
    public Node createNode() {
        return new Node();
    }

    /**
     * Create an instance of {@link NodeRole }
     * 
     */
    public NodeRole createNodeRole() {
        return new NodeRole();
    }

    /**
     * Create an instance of {@link RecordPattern }
     * 
     */
    public RecordPattern createRecordPattern() {
        return new RecordPattern();
    }

    /**
     * Create an instance of {@link History }
     * 
     */
    public History createHistory() {
        return new History();
    }

    /**
     * Create an instance of {@link EventData }
     * 
     */
    public EventData createEventData() {
        return new EventData();
    }

    /**
     * Create an instance of {@link Method }
     * 
     */
    public Method createMethod() {
        return new Method();
    }

    /**
     * Create an instance of {@link Reference }
     * 
     */
    public Reference createReference() {
        return new Reference();
    }

    /**
     * Create an instance of {@link Flow }
     * 
     */
    public Flow createFlow() {
        return new Flow();
    }

    /**
     * Create an instance of {@link System }
     * 
     */
    public System createSystem() {
        return new System();
    }

    /**
     * Create an instance of {@link Service }
     * 
     */
    public Service createService() {
        return new Service();
    }

    /**
     * Create an instance of {@link Expectation }
     * 
     */
    public Expectation createExpectation() {
        return new Expectation();
    }

    /**
     * Create an instance of {@link Record }
     * 
     */
    public Record createRecord() {
        return new Record();
    }

    /**
     * Create an instance of {@link RecordData }
     * 
     */
    public RecordData createRecordData() {
        return new RecordData();
    }

    /**
     * Create an instance of {@link AlternativeID }
     * 
     */
    public AlternativeID createAlternativeID() {
        return new AlternativeID();
    }

    /**
     * Create an instance of {@link IODEFDocument }
     * 
     */
    public IODEFDocument createIODEFDocument() {
        return new IODEFDocument();
    }

    /**
     * Create an instance of {@link Incident }
     * 
     */
    public Incident createIncident() {
        return new Incident();
    }

    /**
     * Create an instance of {@link RelatedActivity }
     * 
     */
    public RelatedActivity createRelatedActivity() {
        return new RelatedActivity();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MLStringType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:ietf:params:xml:ns:iodef-1.0", name = "Location")
    public JAXBElement<MLStringType> createLocation(MLStringType value) {
        return new JAXBElement<MLStringType>(_Location_QNAME, MLStringType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ExtensionType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:ietf:params:xml:ns:iodef-1.0", name = "RecordItem")
    public JAXBElement<ExtensionType> createRecordItem(ExtensionType value) {
        return new JAXBElement<ExtensionType>(_RecordItem_QNAME, ExtensionType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MLStringType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:ietf:params:xml:ns:iodef-1.0", name = "ContactName")
    public JAXBElement<MLStringType> createContactName(MLStringType value) {
        return new JAXBElement<MLStringType>(_ContactName_QNAME, MLStringType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SoftwareType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:ietf:params:xml:ns:iodef-1.0", name = "Application")
    public JAXBElement<SoftwareType> createApplication(SoftwareType value) {
        return new JAXBElement<SoftwareType>(_Application_QNAME, SoftwareType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ContactMeansType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:ietf:params:xml:ns:iodef-1.0", name = "Fax")
    public JAXBElement<ContactMeansType> createFax(ContactMeansType value) {
        return new JAXBElement<ContactMeansType>(_Fax_QNAME, ContactMeansType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:ietf:params:xml:ns:iodef-1.0", name = "ReportTime")
    public JAXBElement<XMLGregorianCalendar> createReportTime(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_ReportTime_QNAME, XMLGregorianCalendar.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:ietf:params:xml:ns:iodef-1.0", name = "DateTime")
    public JAXBElement<XMLGregorianCalendar> createDateTime(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_DateTime_QNAME, XMLGregorianCalendar.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link IncidentIDType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:ietf:params:xml:ns:iodef-1.0", name = "IncidentID")
    public JAXBElement<IncidentIDType> createIncidentID(IncidentIDType value) {
        return new JAXBElement<IncidentIDType>(_IncidentID_QNAME, IncidentIDType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:ietf:params:xml:ns:iodef-1.0", name = "URL")
    public JAXBElement<String> createURL(String value) {
        return new JAXBElement<String>(_URL_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:ietf:params:xml:ns:iodef-1.0", name = "DetectTime")
    public JAXBElement<XMLGregorianCalendar> createDetectTime(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_DetectTime_QNAME, XMLGregorianCalendar.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:ietf:params:xml:ns:iodef-1.0", name = "StartTime")
    public JAXBElement<XMLGregorianCalendar> createStartTime(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_StartTime_QNAME, XMLGregorianCalendar.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ContactMeansType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:ietf:params:xml:ns:iodef-1.0", name = "Telephone")
    public JAXBElement<ContactMeansType> createTelephone(ContactMeansType value) {
        return new JAXBElement<ContactMeansType>(_Telephone_QNAME, ContactMeansType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:ietf:params:xml:ns:iodef-1.0", name = "EndTime")
    public JAXBElement<XMLGregorianCalendar> createEndTime(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_EndTime_QNAME, XMLGregorianCalendar.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ContactMeansType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:ietf:params:xml:ns:iodef-1.0", name = "Email")
    public JAXBElement<ContactMeansType> createEmail(ContactMeansType value) {
        return new JAXBElement<ContactMeansType>(_Email_QNAME, ContactMeansType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MLStringType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:ietf:params:xml:ns:iodef-1.0", name = "Description")
    public JAXBElement<MLStringType> createDescription(MLStringType value) {
        return new JAXBElement<MLStringType>(_Description_QNAME, MLStringType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:ietf:params:xml:ns:iodef-1.0", name = "Timezone")
    public JAXBElement<String> createTimezone(String value) {
        return new JAXBElement<String>(_Timezone_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SoftwareType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:ietf:params:xml:ns:iodef-1.0", name = "OperatingSystem")
    public JAXBElement<SoftwareType> createOperatingSystem(SoftwareType value) {
        return new JAXBElement<SoftwareType>(_OperatingSystem_QNAME, SoftwareType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ExtensionType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:ietf:params:xml:ns:iodef-1.0", name = "AdditionalData")
    public JAXBElement<ExtensionType> createAdditionalData(ExtensionType value) {
        return new JAXBElement<ExtensionType>(_AdditionalData_QNAME, ExtensionType.class, null, value);
    }

}
