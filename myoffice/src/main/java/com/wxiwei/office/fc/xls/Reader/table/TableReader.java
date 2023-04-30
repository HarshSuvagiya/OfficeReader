/*
 * 文件名称:          TableReader.java
 *  
 * 编译器:            android2.2
 * 时间:              下午4:18:27
 */
package com.wxiwei.office.fc.xls.Reader.table;

import java.io.InputStream;

import com.wxiwei.office.fc.dom4j.Document;
import com.wxiwei.office.fc.dom4j.Element;
import com.wxiwei.office.fc.dom4j.io.SAXReader;
import com.wxiwei.office.fc.openxml4j.opc.PackagePart;
import com.wxiwei.office.ss.model.CellRangeAddress;
import com.wxiwei.office.ss.model.baseModel.Sheet;
import com.wxiwei.office.ss.model.table.SSTable;
import com.wxiwei.office.ss.util.ReferenceUtil;
import com.wxiwei.office.system.IControl;

/**
 * TODO: 文件注释
 * <p>
 * <p>
 * Read版本:        Read V1.0
 * <p>
 * 作者:            jqin
 * <p>
 * 日期:            2013-4-17
 * <p>
 * 负责人:           jqin
 * <p>
 * 负责小组:           
 * <p>
 * <p>
 */
public class TableReader
{
    private static TableReader reader = new TableReader();
    
    /**
     * 
     */
    public static TableReader instance()
    {
        return reader;
    }
    
    public void read(IControl control, PackagePart tablePart, Sheet sheet) throws Exception
    {
        SAXReader saxreader = new SAXReader();  
        try
        {             
            InputStream in = tablePart.getInputStream();
            Document talbeDoc = saxreader.read(in);                
            in.close();
            
            SSTable table = new  SSTable();
            Element root = talbeDoc.getRootElement();
            //table ref
            String ref = root.attributeValue("ref");
            String[] rang = ref.split(":");
            if(rang != null && rang.length == 2)
            {
                table.setTableReference(
                    new CellRangeAddress(ReferenceUtil.instance().getRowIndex(rang[0]), 
                        ReferenceUtil.instance().getColumnIndex(rang[0]),
                        ReferenceUtil.instance().getRowIndex(rang[1]), 
                        ReferenceUtil.instance().getColumnIndex(rang[1])));
            }
            
            //totalsRowDxfId
            String str = root.attributeValue("totalsRowDxfId");
            if(str != null)
            {
                try {
                    table.setTotalsRowDxfId(Integer.parseInt(str));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
            
            //totalsRowBorderDxfId
            str = root.attributeValue("totalsRowBorderDxfId");
            if(str != null)
            {
                try {
                    table.setTotalsRowBorderDxfId(Integer.parseInt(str));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
            
            //headerRowDxfId
            str = root.attributeValue("headerRowDxfId");
            if(str != null)
            {
                try {
                    table.setHeaderRowDxfId(Integer.parseInt(str));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
            
            //headerRowBorderDxfId
            str = root.attributeValue("headerRowBorderDxfId");
            if(str != null)
            {
                try {
                    table.setHeaderRowBorderDxfId(Integer.parseInt(str));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
            
            //tableBorderDxfId
            str = root.attributeValue("tableBorderDxfId");
            if(str != null)
            {
                try {
                    table.setTableBorderDxfId(Integer.parseInt(str));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
            
            String headerRowCount = root.attributeValue("headerRowCount");
            if("0".equalsIgnoreCase(headerRowCount))
            {
                table.setHeaderRowShown(false);
            }
            
            String totalsRowCount = root.attributeValue("totalsRowCount");
            if(totalsRowCount == null)
            {
                totalsRowCount = "0";
            }
            String totalsRowShown = root.attributeValue("totalsRowShown");
            if(!"0".equalsIgnoreCase(totalsRowShown) 
                && "1".equalsIgnoreCase(totalsRowCount))
            {
                table.setTotalRowShown(true);
            }
            
            Element tableStyleInfo = root.element("tableStyleInfo");
            if(tableStyleInfo != null)
            {
                String name = tableStyleInfo.attributeValue("name");
                table.setName(name);
                
                String showFirstColumn = tableStyleInfo.attributeValue("showFirstColumn");
                if(!"0".equalsIgnoreCase(showFirstColumn))
                {
                    table.setShowFirstColumn(true);
                }
                String showLastColumn = tableStyleInfo.attributeValue("showLastColumn");
                if(!"0".equalsIgnoreCase(showLastColumn))
                {
                    table.setShowLastColumn(true);
                }
                String showRowStripes = tableStyleInfo.attributeValue("showRowStripes");
                if(!"0".equalsIgnoreCase(showRowStripes))
                {
                    table.setShowRowStripes(true);
                }
                String showColumnStripes = tableStyleInfo.attributeValue("showColumnStripes");
                if(!"0".equalsIgnoreCase(showColumnStripes))
                {
                    table.setShowColumnStripes(true);
                }
                
                sheet.addTable(table);
            }
        }
        catch (Exception  e)
        {
            throw e;
        }
        finally
        {
            saxreader.resetHandlers();
        }
    }
}
