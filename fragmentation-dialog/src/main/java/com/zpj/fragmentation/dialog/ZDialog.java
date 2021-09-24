package com.zpj.fragmentation.dialog;

import com.zpj.fragmentation.dialog.impl.AlertDialogFragment;
import com.zpj.fragmentation.dialog.impl.ArrowMenuDialogFragment;
import com.zpj.fragmentation.dialog.impl.AttachListDialogFragment;
import com.zpj.fragmentation.dialog.impl.BottomListDialogFragment;
import com.zpj.fragmentation.dialog.impl.BottomSelectDialogFragment;
import com.zpj.fragmentation.dialog.impl.CheckDialogFragment;
import com.zpj.fragmentation.dialog.impl.ImageViewerDialogFragment;
import com.zpj.fragmentation.dialog.impl.InputDialogFragment;
import com.zpj.fragmentation.dialog.impl.ListDialogFragment;
import com.zpj.fragmentation.dialog.impl.LoadingDialogFragment;
import com.zpj.fragmentation.dialog.impl.SelectDialogFragment;
import com.zpj.fragmentation.dialog.impl.SimpleSelectDialogFragment;

public class ZDialog {

    public static class AlertDialogFragmentImpl extends AlertDialogFragment<AlertDialogFragmentImpl> {

    }

    public static class ListDialogFragmentImpl<T> extends ListDialogFragment<T, ListDialogFragmentImpl<T>> {

    }

    public static class SelectDialogFragmentImpl<T> extends SelectDialogFragment<T, SelectDialogFragmentImpl<T>> {

    }

    public static class BottomListDialogFragmentImpl<T> extends BottomListDialogFragment<T, BottomListDialogFragmentImpl<T>> {

    }

    public static class BottomSelectDialogFragmentImpl<T> extends BottomSelectDialogFragment<T, BottomSelectDialogFragmentImpl<T>> {

    }

    public static AlertDialogFragment<AlertDialogFragmentImpl> alert() {
        return new AlertDialogFragmentImpl();
    }

    public static CheckDialogFragment check() {
        return new CheckDialogFragment();
    }

    public static InputDialogFragment input() {
        return new InputDialogFragment();
    }

    public static LoadingDialogFragment loading() {
        return new LoadingDialogFragment();
    }

    public static <T> ImageViewerDialogFragment<T> imageViewer(Class<T> tClass) {
        return new ImageViewerDialogFragment<>();
    }

    public static ImageViewerDialogFragment<String> imageViewer() {
        return imageViewer(String.class);
    }

    public static <T> SelectDialogFragmentImpl<T> select(Class<T> tClass) {
        return new SelectDialogFragmentImpl<>();
    }

    public static SelectDialogFragmentImpl<String> select() {
        return select(String.class);
    }

    public static SimpleSelectDialogFragment simpleSelect() {
        return new SimpleSelectDialogFragment();
    }

    public static <T> ListDialogFragmentImpl<T> list(Class<T> tClass) {
        return new ListDialogFragmentImpl<>();
    }

    public static ListDialogFragmentImpl<String> list() {
        return list(String.class);
    }

    public static <T> BottomSelectDialogFragmentImpl<T> bottomSelect(Class<T> tClass) {
        return new BottomSelectDialogFragmentImpl<>();
    }

    public static BottomSelectDialogFragmentImpl<String> bottomSelect() {
        return bottomSelect(String.class);
    }

    public static <T> BottomListDialogFragmentImpl<T> bottomList(Class<T> tClass) {
        return new BottomListDialogFragmentImpl<>();
    }

    public static BottomListDialogFragmentImpl<String> bottomList() {
        return bottomList(String.class);
    }

    public static ArrowMenuDialogFragment arrowMenu() {
        return new ArrowMenuDialogFragment();
    }

    public static <T> AttachListDialogFragment<T> attach(Class<T> tClass) {
        return new AttachListDialogFragment<T>();
    }

    public static AttachListDialogFragment<String> attach() {
        return attach(String.class);
    }

}
