package estrategiamovil.comerciomovil.tools;

import java.util.ArrayList;
import java.util.Iterator;

import estrategiamovil.comerciomovil.modelo.AskQuestion;
import estrategiamovil.comerciomovil.modelo.PendingPublication;
import estrategiamovil.comerciomovil.modelo.PublicationCardViewModel;
import estrategiamovil.comerciomovil.modelo.PublicationUser;
import estrategiamovil.comerciomovil.modelo.PurchaseItem;
import estrategiamovil.comerciomovil.modelo.RatingItem;
import estrategiamovil.comerciomovil.modelo.SalesItem;

/**
 * Created by administrator on 22/03/2017.
 */
public class GeneralFunctions {

    public static ArrayList<PublicationCardViewModel> FilterPublications(ArrayList<PublicationCardViewModel> publications, ArrayList<PublicationCardViewModel> new_publications){
        Iterator<PublicationCardViewModel> iter = new_publications.iterator();
        while (iter.hasNext()){
            PublicationCardViewModel p = iter.next();
            if (publications.contains(p)){ iter.remove();}
        }
        return new_publications;
    }
    public static ArrayList<PendingPublication> FilterPendingPublications(ArrayList<PendingPublication> publications, ArrayList<PendingPublication> new_publications){
        Iterator<PendingPublication> iter = new_publications.iterator();
        while (iter.hasNext()){
            PendingPublication p = iter.next();
            if (publications.contains(p)){ iter.remove();}
        }
        return new_publications;
    }
    public static ArrayList<RatingItem> FilterReviews(ArrayList<RatingItem> publications, ArrayList<RatingItem> new_reviews){
        Iterator<RatingItem> iter = new_reviews.iterator();
        while (iter.hasNext()){
            RatingItem p = iter.next();
            if (publications.contains(p)){ iter.remove();}
        }
        return new_reviews;
    }
    public static ArrayList<AskQuestion> FilterAsks(ArrayList<AskQuestion> publications, ArrayList<AskQuestion> new_asks){
        Iterator<AskQuestion> iter = new_asks.iterator();
        while (iter.hasNext()){
            AskQuestion p = iter.next();
            if (publications.contains(p)){ iter.remove();}
        }
        return new_asks;
    }
    public static ArrayList<PublicationUser> FilterPublicationsMerchant(ArrayList<PublicationUser> publications, ArrayList<PublicationUser> new_asks){
        Iterator<PublicationUser> iter = new_asks.iterator();
        while (iter.hasNext()){
            PublicationUser p = iter.next();
            if (publications.contains(p)){ iter.remove();}
        }
        return new_asks;
    }
    public static ArrayList<SalesItem> FilterSales(ArrayList<SalesItem> publications, ArrayList<SalesItem> new_asks){
        Iterator<SalesItem> iter = new_asks.iterator();
        while (iter.hasNext()){
            SalesItem p = iter.next();
            if (publications.contains(p)){ iter.remove();}
        }
        return new_asks;
    }
    public static ArrayList<PurchaseItem> FilterPurchases(ArrayList<PurchaseItem> publications, ArrayList<PurchaseItem> new_asks){
        Iterator<PurchaseItem> iter = new_asks.iterator();
        while (iter.hasNext()){
            PurchaseItem p = iter.next();
            if (publications.contains(p)){ iter.remove();}
        }
        return new_asks;
    }
}
