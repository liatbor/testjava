package customer.testjava.handlers.external;

import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.sap.cds.Result;
import com.sap.cds.services.cds.CqnService;
import com.sap.cds.services.cds.CdsReadEventContext;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.After;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;
import cds.gen.catalogservice.CatalogService_;
import cds.gen.catalogservice.Books;

import cds.gen.catalogservice.Addresses_;
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

    @Qualifier(Addresses_.CDS_NAME)

    @On(event = CqnService.EVENT_READ, entity = Addresses_.CDS_NAME)
    public void readAddresses(CdsReadEventContext ctx) {
		Result result = bupa.run(ctx.getCqn());
		ctx.setResult(result);
		ctx.setCompleted();
	}

}