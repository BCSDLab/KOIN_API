package koreatech.in.domain.BokDuck;

public class LandResponseType {
    private static String[] toAdminArray = new String[] {
    };

    private static String[] toArray = new String[] {
            "class",
            "size",
            "phone",
            "image_urls",
            "address",
            "description",
            "floor",
            "deposit",
            "management_fee",
            "opt_refrigerator",
            "opt_closet",
            "opt_tv",
            "opt_microwave",
            "opt_gas_range",
            "opt_induction",
            "opt_water_purifier",
            "opt_air_conditioner",
            "opt_washer",
            "opt_bed",
            "opt_desk",
            "opt_shoe_closet",
            "opt_electronic_door_locks",
            "opt_bidet",
            "opt_veranda",
            "opt_elevator",
            "is_deleted",
            "created_at",
            "updated_at",
    };

    public static String[] getAdminArray () {
        return toAdminArray;
    }

    public static String[] getArray () {
        return toArray;
    }
}
