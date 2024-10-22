package customer.testjava.handlers.external;

import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.sap.cds.Result;
import com.sap.cds.ql.CQL;
import com.sap.cds.ql.cqn.CqnSelect;
import com.sap.cds.ql.Insert;
import com.sap.cds.ql.Predicate;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.Upsert;
import com.sap.cds.ql.cqn.Modifier;
import com.sap.cds.services.cds.CqnService;
import com.sap.cds.services.cds.RemoteService;
import com.sap.cds.services.ErrorStatuses;
import com.sap.cds.services.cds.CdsReadEventContext;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.After;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.ServiceException;

import cds.gen.catalogservice.CatalogService_;
import cds.gen.catalogservice.Books;

import cds.gen.catalogservice.Addresses_;
import cds.gen.catalogservice.Addresses;
import cds.gen.api_business_partner.ApiBusinessPartner_;
import cds.gen.api_business_partner.ApiBusinessPartner;

@Component
@ServiceName(CatalogService_.CDS_NAME)
public class CatalogServiceHandler implements EventHandler {

	private final ApiBusinessPartner bupa;

	CatalogServiceHandler(@Qualifier(ApiBusinessPartner_.CDS_NAME) ApiBusinessPartner bupa) {
		this.bupa = bupa;
	}

	@After(event = CqnService.EVENT_READ)
	public void discountBooks(Stream<Books> books) {
		books.filter(b -> b.getTitle() != null && b.getStock() != null)
		.filter(b -> b.getStock() > 200)
		.forEach(b -> b.setTitle(b.getTitle() + " (discounted)"));
	}

	// Delegate ValueHelp requests to S/4 backend, fetching current user's addresses from there
	// @On(event = CqnService.EVENT_READ, entity = Addresses_.CDS_NAME)
	// public void readAddresses(CdsReadEventContext context) {
	// 	if(context.getCqn().ref().segments().size() != 1) {
	// 		return; // no value help request
	// 	}

	// 	// add BusinessPartner where condition
	// 	String businessPartner = context.getUserInfo().getAttributeValues("businessPartner").stream().findFirst().orElseThrow(() -> new ServiceException(ErrorStatuses.FORBIDDEN, "BUPA_MISSING"));

	// 	CqnSelect select = CQL.copy(context.getCqn(), new Modifier() {

	// 		public Predicate where(Predicate original) {
	// 			Predicate where = CQL.get(Addresses.BUSINESS_PARTNER).eq(businessPartner);
	// 			if(original != null) {
	// 				where = original.and(where);
	// 			}
	// 			return where;
	// 		}

	// 	});
	// }

	//@Autowired
    @Qualifier(Addresses_.CDS_NAME)
    //private RemoteService remoteService;

    @On(event = CqnService.EVENT_READ, entity = Addresses_.CDS_NAME)
    public void readAddresses(CdsReadEventContext ctx) {
		// Result result = remoteService.run(ctx.getCqn());
		Result result = bupa.run(ctx.getCqn());
		ctx.setResult(result);
		ctx.setCompleted();
	}

}