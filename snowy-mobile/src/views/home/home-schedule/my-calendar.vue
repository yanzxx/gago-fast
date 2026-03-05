<template>
  <div class="myCalendar">
    <Calendar
        backgroundText
        :selectMode="'select'"
        class-name="select-mode"
        :lunar="lunar"
        :select-date="selectModeDate"
        completion="true"
        @onSelect="onSelect"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, defineEmits } from "vue";
import Calendar from 'mpvue-calendar';
import lunar from 'mpvue-calendar/dist/lunar';

const currentDate = new Date();
const currentYear = currentDate.getFullYear();
const currentMonth = currentDate.getMonth() + 1;
const currentDay = currentDate.getDate();

const selectModeDate = ref(`${currentYear}-${currentMonth}-${currentDay}`);

const emits = defineEmits(["onSelectDay"])

onMounted(() => {
  onSelect(selectModeDate.value)
})

const onSelect = (day) => {
  emits('onSelectDay', day)
}


</script>

<style lang="scss">

.myCalendar {
  .vc-calendar-date {
    top: 11% !important;
    transform: none !important;
  }

  .vc-day-selected {
    color: #fff !important;
  }

  .vc-calendar-today {
    color: #2979ff !important;

    &.vc-day-selected {
      color: #fff !important;
    }
  }

  .vc-calendar-almanac {
    bottom: 14% !important;
  }

  .vc-day-selected:before {
    background: #6792F8 !important;
  }

}

</style>