using my.bookshop as my from '../db/data-model';
using API_BUSINESS_PARTNER as external from './external/API_BUSINESS_PARTNER';

service CatalogService {
    @readonly
    entity Books     as projection on my.Books;


    @cds.persistence: {
        skip: true
    } 
    @cds.autoexpose //> auto-expose in services as targets for ValueHelps and joins
    entity Addresses as
        projection on external.A_BusinessPartnerAddress {
            key AddressID             as ID,
            key BusinessPartner       as businessPartner,
                @readonly Country     as country,
                @readonly CityName    as city,
                @readonly PostalCode  as postalCode,
                @readonly StreetName  as street,
                @readonly HouseNumber as houseNumber,
                false                 as tombstone : Boolean
        }
}
