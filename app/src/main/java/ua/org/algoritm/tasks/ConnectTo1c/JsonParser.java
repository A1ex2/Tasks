package ua.org.algoritm.tasks.ConnectTo1c;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import ua.org.algoritm.tasks.DetailsOrder;

public class JsonParser {

    public static DetailsOrder.Order getOrder(String response) throws JSONException {
        DetailsOrder.Order order = new DetailsOrder.Order();

        JSONObject jOrder = new JSONObject(response);
        order.setID(jOrder.getString("ID"));
        order.setDescription(jOrder.getString("Description"));
        order.setStatusID(jOrder.getString("StatusID"));
        order.setStatus(jOrder.getString("Status"));

        JSONArray availableStatusJSON = new JSONArray(jOrder.getString("AvailableStatus"));

        HashMap<String, String> availableStatus = new HashMap<>();
        ArrayList<String> mStatus = new ArrayList<>();

        for (int i = 0; i <availableStatusJSON.length(); i++) {
            JSONObject jStatus = new JSONObject(availableStatusJSON.get(i).toString());
            availableStatus.put(jStatus.getString("Name"), jStatus.getString("Synonym"));

            mStatus.add(jStatus.getString("Synonym"));
        }
        order.setAvailableStatus(availableStatus);
        order.setStringStatus(mStatus);

        return order;
    }


//    public static ArrayList<OrderOutfit> getOrderOutfit(String response) throws JSONException {
//        ArrayList<OrderOutfit> mOrderOutfits = new ArrayList<>();
//        JSONArray ordersJSON = new JSONArray(response);
//
//        for (int i = 0; i < ordersJSON.length(); i++) {
//            OrderOutfit orderOutfit = new OrderOutfit();
//            JSONObject order = new JSONObject(ordersJSON.get(i).toString());
//
//            orderOutfit.setID(order.getString("ID"));
//            orderOutfit.setDescription(order.getString("Description"));
//            orderOutfit.setResponsibleID(order.getString("ResponsibleID"));
//            orderOutfit.setResponsible(order.getString("Responsible"));
//            orderOutfit.setStateID(order.getString("StateID"));
//            orderOutfit.setState(order.getString("State"));
//
//            JSONArray CarDataOutfitsJSON = new JSONArray(order.getString("CarDataOutfit"));
//            ArrayList<CarDataOutfit> mCarDataOutfits = new ArrayList<>();
//            for (int j = 0; j < CarDataOutfitsJSON.length(); j++) {
//                JSONObject car = new JSONObject(CarDataOutfitsJSON.get(j).toString());
//
//                CarDataOutfit mCar = new CarDataOutfit();
//                mCar.setCarID(car.getString("CarID"));
//                mCar.setCar(car.getString("Car"));
//                mCar.setSectorID(car.getString("SectorID"));
//                mCar.setSector(car.getString("Sector"));
//                mCar.setRow(car.getString("Row"));
//                mCar.setBarCode(car.getString("BarCode"));
//
//                JSONArray OperationsJSON = new JSONArray(car.getString("Operations"));
//                ArrayList<OperationOutfits> mOperation = new ArrayList<>();
//                for (int k = 0; k < OperationsJSON.length(); k++) {
//                    JSONObject operation = new JSONObject(OperationsJSON.get(k).toString());
////                    OperationOutfits mOperationOutfits = new OperationOutfits();
//                    mOperationOutfits.setOperationID(operation.getString("OperationID"));
//                    mOperationOutfits.setOperation(operation.getString("Operation"));
//                    mOperationOutfits.setPerformed(operation.getBoolean("Performed"));
//                    mOperationOutfits.setQuantityPhoto(operation.getInt("QuantityPhoto"));
//
//                    mOperation.add(mOperationOutfits);
//                }
//
//                mCar.setOperations(mOperation);
//
//                mCarDataOutfits.add(mCar);
//            }
//            orderOutfit.setCarDataOutfit(mCarDataOutfits);
//
//            mOrderOutfits.add(orderOutfit);
//        }
//        return mOrderOutfits;
//    }

}
