package com.cocosw.xteam.data;

import java.io.Serializable;
import java.util.List;

/**
 * Coco studio
 * <p/>
 * Created by kai on 1/12/2015.
 */
public class Emotion implements Serializable{
        public String id;
        public String type;
        public int size;
        public int price;
        public String face;
        public int stock;
        public List<String> tags;
}
