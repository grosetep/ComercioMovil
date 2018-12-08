package estrategiamovil.comerciomovil.ui.adapters;

/**
 * Created by administrator on 17/08/2016.
 */
/*
public class SelectCategoryAdapter extends RecyclerView.Adapter<SelectCategoryAdapter.CategoryViewHolder> {
    private static final String TAG = CategoryAdapter.class.getSimpleName();
    private final LayoutInflater mInflater;
    private final List<CategoryViewModel> mModels;
    private int mBackground;
    private final TypedValue mTypedValue = new TypedValue();
    private HashMap<String , String> idsDragableCategories = new HashMap<>();

    public class CategoryViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvText;
        private String idCategory;
        private CircleImageView imageViewCircle;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            tvText = (TextView) itemView.findViewById(R.id.text_category);
            idCategory = "";
            imageViewCircle = (CircleImageView)itemView.findViewById(R.id.image_category);
        }

        public void bind(CategoryViewModel model) {
            tvText.setText(model.getCategory());
            idCategory = model.getIdCategory();
        }


    }

    public SelectCategoryAdapter(Context context, List<CategoryViewModel> models) {
        context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
        mBackground = mTypedValue.resourceId;
        mInflater = LayoutInflater.from(context);
        mModels = new ArrayList<>(models);
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = mInflater.inflate(R.layout.select_category_layout, parent, false);
        itemView.setBackgroundResource(mBackground);
        return new CategoryViewHolder(itemView);
    }
    private int getRandomShape() {
        int dg;
        Random random = new Random();

        switch( random.nextInt(1)){
            case 1: dg = R.drawable.shape_icon_blue;
                break;
            case 2: dg = R.drawable.shape_icon_green;
                break;
            case 3: dg = R.drawable.shape_icon_orange;
                break;
            case 4: dg = R.drawable.shape_icon_yellow;
                break;
            case 5: dg = R.drawable.shape_icon_red;
                break;
            default:
                dg = R.drawable.shape_icon_blue;
        }

        return dg;
    }
    @Override
    public void onBindViewHolder(final CategoryViewHolder holder, int position) {
        Context context = holder.imageViewCircle.getContext();
        final CategoryViewModel model = mModels.get(position);

        holder.bind(model);
        holder.imageViewCircle.setBackgroundResource(getRandomShape());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, SelectSubCategoryActivity.class);
                intent.putExtra(Constants.CATEGORY_SELECTED,model.getIdCategory());
                context.startActivity(intent);
            }
        });

        Glide.with(holder.imageViewCircle.getContext())
                .load(mModels.get(position).getPathImageCategory().equals(Constants.imageCategoryInLocalPath)? new Integer(idsDragableCategories.get(mModels.get(position).getIdCategory())):idsDragableCategories.get(mModels.get(position).getIdCategory())) //mModels.get(position).getPathImageCategory() + mModels.get(position).getImageCategory()
                .fitCenter()
                .into(holder.imageViewCircle);
    }
    public void setIdsDrawableCategories(HashMap<String , String> mapIconsDrawables){
        this.idsDragableCategories = mapIconsDrawables;
    }
    @Override
    public int getItemCount() {
        return mModels.size();
    }

    public void animateTo(List<CategoryViewModel> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateRemovals(List<CategoryViewModel> newModels) {
        for (int i = mModels.size() - 1; i >= 0; i--) {
            final CategoryViewModel model = mModels.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<CategoryViewModel> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final CategoryViewModel model = newModels.get(i);
            if (!mModels.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<CategoryViewModel> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final CategoryViewModel model = newModels.get(toPosition);
            final int fromPosition = mModels.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    public CategoryViewModel removeItem(int position) {
        final CategoryViewModel model = mModels.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    public void addItem(int position, CategoryViewModel model) {
        mModels.add(position, model);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final CategoryViewModel model = mModels.remove(fromPosition);
        mModels.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }


}*/
