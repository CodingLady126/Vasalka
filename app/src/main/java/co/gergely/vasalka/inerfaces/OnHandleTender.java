package co.gergely.vasalka.inerfaces;

import co.gergely.vasalka.model.Tender;

public interface OnHandleTender {
    void onTenderClicked(Tender tender);

    void onOpenTender(Long id);
    void onApplyTender(Tender tender);
}
