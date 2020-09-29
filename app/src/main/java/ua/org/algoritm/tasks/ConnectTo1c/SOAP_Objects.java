package ua.org.algoritm.tasks.ConnectTo1c;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

public class SOAP_Objects {
    public static String getOrderStatus(String id, String statusID) {
        JSON jsonReception = new JSON(id, statusID);
        Gson gson = new GsonBuilder().create();
        String stringReception = gson.toJson(jsonReception);
        return stringReception;
    }

    private static class JSON {
        @SerializedName("id")
        String id;
        @SerializedName("statusID")
        String statusID;

        JSON(String id, String statusID) {
            this.id = id;
            this.statusID = statusID;
        }
    }


//    public static String getReception(Reception reception) {
//        JSONReception jsonReception = new JSONReception(reception);
//        Gson gson = new GsonBuilder().create();
//        String stringReception = gson.toJson(jsonReception);
//        return stringReception;
//    }
//
//    public static String getCarData(CarData carData) {
//
//        JSONCarData jsonCarData = new JSONCarData(carData);
//        Gson gson = new GsonBuilder().create();
//        String stringCarData = gson.toJson(jsonCarData);
//        return stringCarData;
//
//    }
//
//    public static String getCarDataIssuance(CarDataIssuance carData) {
//
//        JSONCarDataIssuance jsonCarDataIssuance = new JSONCarDataIssuance(carData);
//        Gson gson = new GsonBuilder().create();
//        String stringCarData = gson.toJson(jsonCarDataIssuance);
//        return stringCarData;
//
//    }
//
//    public static String getCarDataOutfit(String orderID, CarDataOutfit carDataOutfit) {
//
//        JSONCarDataOutfit jsonCarData = new JSONCarDataOutfit(orderID, carDataOutfit);
//        Gson gson = new GsonBuilder().create();
//        String stringCarData = gson.toJson(jsonCarData);
//        return stringCarData;
//
//    }
//
//    public static String getOrderOutfit(OrderOutfit mOutfit) {
//
//        JSONOutfit jsonOutfit = new JSONOutfit(mOutfit);
//        Gson gson = new GsonBuilder().create();
//        String stringCarData = gson.toJson(jsonOutfit);
//        return stringCarData;
//
//    }
//
//    private static class JSONReception {
//        @SerializedName("ID")
//        String ID;
//
//        @SerializedName("CarData")
//        ArrayList<JSONCarData> mJSONCarData = new ArrayList<>();
//
//        JSONReception(Reception reception) {
//            ID = reception.getID();
//
//            ArrayList<CarData> carDataArrayList = reception.getCarData();
//            for (int i = 0; i < carDataArrayList.size(); i++) {
//                CarData carData = carDataArrayList.get(i);
//                JSONCarData jsonCarData = new JSONCarData(carData);
//                mJSONCarData.add(jsonCarData);
//
//                if (!carData.getRow().equals("") | !carData.getSector().equals("")) {
//                    carData.saveCB = true;
//                }
//            }
//        }
//    }
//
//    private static class JSONCarData {
//        @SerializedName("ReceptionID")
//        String ReceptionID;
//
//        @SerializedName("carID")
//        String carID;
//
//        @SerializedName("barCode")
//        String barCode;
//
//        @SerializedName("sectorID")
//        String sectorID;
//
//        @SerializedName("row")
//        String row;
//
//        @SerializedName("productionDate")
//        String productionDate;
//
//        JSONCarData(CarData carData) {
//            ReceptionID = carData.getReceptionID();
//            barCode = carData.getBarCode();
//            carID = carData.getCarID();
//            sectorID = carData.getSectorID();
//            row = carData.getRow();
//            productionDate = carData.getProductionDateString();
//        }
//    }
//
//    private static class JSONCarDataIssuance {
//        @SerializedName("IssuanceID")
//        String IssuanceID;
//
//        @SerializedName("carID")
//        String carID;
//
//        @SerializedName("isMoving")
//        String isMoving;
//
//        @SerializedName("sectorIDMoving")
//        String sectorIDMoving;
//
//        @SerializedName("rowMoving")
//        String rowMoving;
//
//        JSONCarDataIssuance(CarDataIssuance carData) {
//            IssuanceID = carData.getIssuanceID();
//            carID = carData.getCarID();
//
//            isMoving = carData.getMoving();
//            sectorIDMoving = carData.getSectorIDMoving();
//            rowMoving = carData.getRowMoving();
//        }
//    }
//
//    private static class JSONCarDataOutfit {
//        @SerializedName("ID")
//        String ID;
//
//        @SerializedName("carID")
//        String carID;
//
//        @SerializedName("operations")
//        ArrayList<JSONOperation> mJsonOperations = new ArrayList<>();
//
//        @SerializedName("photos")
//        ArrayList<JSONPhoto> mJsonPhotos = new ArrayList<>();
//
//        JSONCarDataOutfit(String orderID, CarDataOutfit carData) {
//            ID = orderID;
//            carID = carData.getCarID();
//
//            ArrayList<OperationOutfits> mOperation = carData.getOperations();
//            for (int i = 0; i < mOperation.size(); i++) {
//                mJsonOperations.add(new JSONOperation(mOperation.get(i)));
//            }
//
//            ArrayList<Photo> mPhotos = carData.getPhoto();
//            for (int i = 0; i < mPhotos.size(); i++) {
//                mJsonPhotos.add(new JSONPhoto(mPhotos.get(i)));
//            }
//        }
//    }
//
//    private static class JSONOperation {
//        @SerializedName("operationID")
//        String OperationID;
//
//        @SerializedName("performed")
//        Boolean Performed;
//
//        JSONOperation(OperationOutfits mOperationOutfits) {
//            OperationID = mOperationOutfits.getOperationID();
//            Performed = mOperationOutfits.getPerformed();
//        }
//    }
//
//    private static class JSONPhoto {
//        @SerializedName("name")
//        String name;
//
//        JSONPhoto(Photo mPhoto) {
//            name = mPhoto.getName();
//        }
//    }
//
//
//    private static class JSONOutfit {
//        @SerializedName("ID")
//        String ID;
//
//        @SerializedName("car")
//        ArrayList<JSONCarOutfit> mJsonCar = new ArrayList<>();
//
//        JSONOutfit(OrderOutfit orderOutfit) {
//            ID = orderOutfit.getID();
//
//            ArrayList<CarDataOutfit> mCarDataOutfit = orderOutfit.getCarDataOutfit();
//            for (int i = 0; i < mCarDataOutfit.size(); i++) {
//                mJsonCar.add(new JSONCarOutfit(mCarDataOutfit.get(i)));
//            }
//        }
//    }
//
//    private static class JSONCarOutfit {
//        @SerializedName("carID")
//        String carID;
//
//        @SerializedName("operations")
//        ArrayList<JSONOperation> mJsonOperations = new ArrayList<>();
//
//        @SerializedName("photos")
//        ArrayList<JSONPhoto> mJsonPhotos = new ArrayList<>();
//
//        JSONCarOutfit(CarDataOutfit mCarDataOutfit) {
//            carID = mCarDataOutfit.getCarID();
//
//            ArrayList<OperationOutfits> mOperation = mCarDataOutfit.getOperations();
//            for (int i = 0; i < mOperation.size(); i++) {
//                mJsonOperations.add(new JSONOperation(mOperation.get(i)));
//            }
//
//            ArrayList<Photo> mPhotos = mCarDataOutfit.getPhoto();
//            for (int i = 0; i < mPhotos.size(); i++) {
//                mJsonPhotos.add(new JSONPhoto(mPhotos.get(i)));
//            }
//        }
//    }
}