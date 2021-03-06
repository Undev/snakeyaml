/**
 * Copyright (c) 2008-2010 Andrey Somov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.yaml.snakeyaml;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

public class JavaBeanLoaderTest extends TestCase {

    public void testLoadString() {
        Bean bean = new Bean();
        bean.setId(3);
        bean.setName("Test me.");
        Yaml yaml = new Yaml();
        String output = yaml.dump(bean);
        assertEquals("!!org.yaml.snakeyaml.JavaBeanLoaderTest$Bean {id: 3, name: Test me.}\n",
                output);
        JavaBeanLoader<Bean> loader = new JavaBeanLoader<Bean>(Bean.class);
        Bean parsed = loader.load(output);
        assertEquals(3, parsed.getId());
        assertEquals("Test me.", parsed.getName());
        // Runtime definition is more important
        JavaBeanLoader<Bean2> loader2 = new JavaBeanLoader<Bean2>(Bean2.class);
        Bean2 parsed2 = loader2.load(output);
        assertEquals(3, parsed2.getId());
        assertEquals("Test me.", parsed2.getName());
        assertFalse(parsed2.isValid());
    }

    public void testLoadInputStream() {
        String yaml = "!!org.yaml.snakeyaml.JavaBeanParserTest$Bean {id: 3, name: Test me.}\n";
        InputStream input = new ByteArrayInputStream(yaml.getBytes());
        JavaBeanLoader<Bean> loader = new JavaBeanLoader<Bean>(Bean.class);
        Bean parsed = loader.load(input);
        assertEquals(3, parsed.getId());
        assertEquals("Test me.", parsed.getName());
    }

    public void testLoadReader() {
        String yaml = "!!org.yaml.snakeyaml.JavaBeanParserTest$Bean {id: 3, name: Test me.}\n";
        Reader input = new StringReader(yaml);
        JavaBeanLoader<Bean> loader = new JavaBeanLoader<Bean>(Bean.class);
        Bean parsed = loader.load(input);
        assertEquals(3, parsed.getId());
        assertEquals("Test me.", parsed.getName());
    }

    public static class Bean {
        private String name;
        private int id;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }

    public static class Bean2 {
        private String name;
        private int id;
        private boolean valid;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public boolean isValid() {
            return valid;
        }

        public void setValid(boolean valid) {
            this.valid = valid;
        }
    }

    public void testTypeDescription1() {
        Bean3 bean3 = new Bean3();
        bean3.setName("Name123");
        Bean bean = new Bean();
        bean.setId(3);
        bean.setName("Test me.");
        bean3.setBean(bean);
        JavaBeanDumper dumper = new JavaBeanDumper();
        String output = dumper.dump(bean3);
        assertEquals("bean:\n  id: 3\n  name: Test me.\nlist: null\nname: Name123\n", output);
        TypeDescription td = new TypeDescription(Bean3.class);
        td.putListPropertyType("list", Integer.class);
        JavaBeanLoader<Bean3> loader = new JavaBeanLoader<Bean3>(td);
        Bean3 parsed = loader.load(output);
        assertEquals("Name123", parsed.getName());
    }

    public void testTypeDescription2() {
        Bean3 bean3 = new Bean3();
        bean3.setName("Name123");
        Bean bean = new Bean();
        bean.setId(3);
        bean.setName("Test me.");
        bean3.setBean(bean);
        List<Integer> list = new ArrayList<Integer>();
        list.add(13);
        list.add(17);
        bean3.setList(list);
        JavaBeanDumper dumper = new JavaBeanDumper();
        String output = dumper.dump(bean3);
        assertEquals("bean:\n  id: 3\n  name: Test me.\nlist:\n- 13\n- 17\nname: Name123\n", output);
        TypeDescription td = new TypeDescription(Bean3.class);
        td.putListPropertyType("list", Integer.class);
        JavaBeanLoader<Bean3> loader = new JavaBeanLoader<Bean3>(td);
        Bean3 parsed = loader.load(output);
        assertEquals("Name123", parsed.getName());
        List<Integer> parsedList = parsed.getList();
        assertEquals(2, parsedList.size());
    }

    public static class Bean3 {
        private String name;
        private Bean bean;
        private List<Integer> list;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Bean getBean() {
            return bean;
        }

        public void setBean(Bean bean) {
            this.bean = bean;
        }

        public List<Integer> getList() {
            return list;
        }

        public void setList(List<Integer> list) {
            this.list = list;
        }
    }
}
